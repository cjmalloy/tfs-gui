package com.cjmalloy.torrentfs.editor.ui.fx.toplevel;

import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.event.DoShutdownNow;
import com.cjmalloy.torrentfs.editor.ui.TopLevel;
import com.cjmalloy.torrentfs.editor.ui.fx.dialog.Dialog;
import com.cjmalloy.torrentfs.editor.ui.fx.view.MainView;
import com.cjmalloy.torrentfs.editor.ui.fx.view.View;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chris on 3/12/2017.
 */
public class MainWindow extends Application implements TopLevel {

  private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);
  private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");
  private static final int INITIAL_WIDTH = 800;
  private static final int INITIAL_HEIGHT = 600;

  private Stage primaryStage = null;
  private View view = null;

  private Thread t;
  private final Object lock = new Object();
  private boolean exitNow = false;

  public MainWindow() {
    Platform.setImplicitExit(false);
    new Thread(() -> Application.launch(MainWindow.class)).start();

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
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;
    primaryStage.setTitle(R.getString("windowTitle"));
    primaryStage.setWidth(INITIAL_WIDTH);
    primaryStage.setHeight(INITIAL_HEIGHT);

    primaryStage.setOnCloseRequest(event -> {
      event.consume();
      MainController.get().requestExit();
    });

    Controller.EVENT_BUS.register(this);

    Dialog.loadAllDialogs(primaryStage);
    setView(new MainView());
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

  public void setView(View v) {
    this.view = v;
    primaryStage.setScene(view.getScene());
  }
}
