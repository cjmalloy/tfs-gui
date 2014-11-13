package com.cjmalloy.torrentfs.editor.event;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;


public class WorkspaceWatcher
{
    private static final int POLL_TIMEOUT = 2000;

    private WatchService watcher = null;
    private Thread watchService = null;
    private volatile boolean stop = false;

    public void setWorkspace(final Path workspace)
    {
        if (getWatcher() == null) return;

        if (watchService != null)
        {
            try
            {
                stop = true;
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
                    WatchKey key = workspace.register(watcher,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY);

                    while (!stop)
                    {
                        key = watcher.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);
                        if (key == null) continue;

                        for (WatchEvent<?> event : key.pollEvents())
                        {
                            WatchEvent.Kind<?> kind = event.kind();
                            if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                            @SuppressWarnings("unchecked")
                            WatchEvent<Path> ev = (WatchEvent<Path>) event;
                            final Path filename = ev.context();

                            SwingUtilities.invokeLater(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    System.out.println("WatchService: " + filename);
                                    // TODO: refresh tree and prompt user
                                }
                            });
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

    public WatchService getWatcher()
    {
        if (watcher == null)
        {
            try
            {
                watcher = FileSystems.getDefault().newWatchService();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        return watcher;
    }
}
