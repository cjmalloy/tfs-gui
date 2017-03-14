package com.cjmalloy.torrentfs.editor.ui.fx.toplevel;

import com.cjmalloy.torrentfs.editor.event.DoShutdownNow;
import com.cjmalloy.torrentfs.editor.ui.TopLevel;
import com.google.common.eventbus.Subscribe;
import javafx.application.Application;
import javafx.application.Platform;

/**
 * Created by chris on 3/12/2017.
 */
public class ApplicationBootstrap implements TopLevel {

  private Thread t;
  private final Object lock = new Object();
  private boolean exitNow = false;

  public ApplicationBootstrap() {
    Platform.setImplicitExit(false);
    new Thread(() -> Application.launch(TfsApplication.class)).start();

    t = new Thread() {
      @Override
      public void run() {
        synchronized (lock) {
          while (!exitNow) {
            try {
              lock.wait();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
    };
    t.start();
  }

  @Override
  public void exitOnFinish() {
    try {
      t.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.exit(0);
  }

  @Subscribe
  public void onShutdownNow(DoShutdownNow event) {
    synchronized (lock) {
      exitNow = true;
      lock.notify();
    }
  }
}
