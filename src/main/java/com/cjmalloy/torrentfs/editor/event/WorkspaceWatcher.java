package com.cjmalloy.torrentfs.editor.event;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.event.FileModificationEvent.FileModification;
import com.cjmalloy.torrentfs.editor.ui.UiUtils;


public class WorkspaceWatcher
{
    private static final int POLL_TIMEOUT = 800;

    private WatchService watcher = null;
    private Thread watchService = null;
    private volatile boolean stop = true;

    public void setWorkspace(final Path workspace)
    {
        if (getWatcher() == null) return;

        if (!stop)
        {
            stop = true;
            try
            {
                watchService.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        stop = false;
        watchService = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    registerAll(workspace);

                    while (!stop)
                    {
                        WatchKey key = watcher.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);
                        if (key == null) continue;

                        for (WatchEvent<?> event : key.pollEvents())
                        {
                            final WatchEvent.Kind<?> kind = event.kind();
                            if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                            WatchEvent<Path> ev = cast(event);
                            Path dir = (Path)key.watchable();
                            final Path child = dir.resolve(ev.context());

                            UiUtils.get().invokeLater(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Controller.EVENT_BUS.post(new FileModificationEvent(child, getType(kind)));
                                }
                            });

                            // if directory is created, and watching recursively, then
                            // register it and its sub-directories
                            if (kind == StandardWatchEventKinds.ENTRY_CREATE)
                            {
                                try
                                {
                                    if (Files.isDirectory(child))
                                    {
                                        registerAll(child);
                                    }
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (!key.reset()) break;
                    }
                }
                catch (InterruptedException | IOException e)
                {
                    e.printStackTrace();
                    return;
                }
            }
        };
        watchService.start();
    }

    private FileModification getType(Kind<?> kind)
    {
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) return FileModification.CREATE;
        if (kind == StandardWatchEventKinds.ENTRY_DELETE) return FileModification.DELETE;
        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) return FileModification.MODIFY;
        throw new Error("unsupported java.nio.file.WatchEvent.Kind<?> type");
    }

    private WatchService getWatcher()
    {
        try
        {
            return watcher = FileSystems.getDefault().newWatchService();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException
    {
        dir.register(watcher,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException
    {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event)
    {
        return (WatchEvent<T>) event;
    }
}
