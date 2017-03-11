package com.cjmalloy.torrentfs.editor.event;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.event.FileModificationEvent.FileModification;
import com.cjmalloy.torrentfs.editor.ui.UiUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;


public class WorkspaceWatcher {
  private static final int POLL_TIMEOUT = 800;

  private WatchService watcher = null;
  private Thread watchService = null;
  private Thread waitingThread = null;
  private volatile boolean stop = true;

  public synchronized void setWorkspace(final Path workspace) {
    if (getWatcher() == null) return;

    // Last thread wins
    waitingThread = Thread.currentThread();
    notifyAll();

    stop = true;
    while (watchService != null) {
      try {
        wait();
      } catch (InterruptedException e) {
      }
      if (waitingThread != Thread.currentThread()) return;
    }

    stop = false;
    if (workspace == null) return;

    watchService = new Thread() {
      @Override
      public void run() {
        try {
          registerAll(workspace);

          while (!stop) {
            WatchKey key = watcher.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);
            if (key == null) continue;

            for (WatchEvent<?> event : key.pollEvents()) {
              final WatchEvent.Kind<?> kind = event.kind();
              if (kind == StandardWatchEventKinds.OVERFLOW) continue;

              WatchEvent<Path> ev = cast(event);
              Path dir = (Path) key.watchable();
              final Path child = dir.resolve(ev.context());

              UiUtils.get().invokeLater(new Runnable() {
                @Override
                public void run() {
                  Controller.EVENT_BUS.post(new FileModificationEvent(child, getType(kind)));
                }
              });

              // if directory is created, and watching recursively, then
              // register it and its sub-directories
              if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                try {
                  if (Files.isDirectory(child)) {
                    registerAll(child);
                  }
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            }

            if (!key.reset()) break;
          }
        } catch (InterruptedException | IOException e) {
          e.printStackTrace();
          return;
        } finally {
          synchronized (WorkspaceWatcher.this) {
            watchService = null;
            WorkspaceWatcher.this.notifyAll();
          }
        }
      }
    };
    watchService.start();
  }

  private FileModification getType(Kind<?> kind) {
    if (kind == StandardWatchEventKinds.ENTRY_CREATE) return FileModification.CREATE;
    if (kind == StandardWatchEventKinds.ENTRY_DELETE) return FileModification.DELETE;
    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) return FileModification.MODIFY;
    throw new Error("unsupported java.nio.file.WatchEvent.Kind<?> type");
  }

  private WatchService getWatcher() {
    try {
      return watcher = FileSystems.getDefault().newWatchService();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Register the given directory with the WatchService
   */
  private void register(Path dir) throws IOException {
    dir.register(watcher,
      StandardWatchEventKinds.ENTRY_CREATE,
      StandardWatchEventKinds.ENTRY_DELETE,
      StandardWatchEventKinds.ENTRY_MODIFY);
  }

  /**
   * Register the given directory, and all its sub-directories, with the
   * WatchService.
   */
  private void registerAll(final Path start) throws IOException {
    // register directory and sub-directories
    Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        register(dir);
        return FileVisitResult.CONTINUE;
      }
    });
  }

  @SuppressWarnings("unchecked")
  static <T> WatchEvent<T> cast(WatchEvent<?> event) {
    return (WatchEvent<T>) event;
  }
}
