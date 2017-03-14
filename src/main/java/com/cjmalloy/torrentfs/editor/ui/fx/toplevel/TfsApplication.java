package com.cjmalloy.torrentfs.editor.ui.fx.toplevel;

import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.stage.Stage;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.ui.fx.dialog.TfsDialog;
import com.cjmalloy.torrentfs.editor.ui.fx.view.MainView;
import com.cjmalloy.torrentfs.editor.ui.fx.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chris on 3/13/2017.
 */
public class TfsApplication extends Application {

  private static final Logger logger = LoggerFactory.getLogger(TfsApplication.class);
  private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");
  private static final int INITIAL_WIDTH = 800;
  private static final int INITIAL_HEIGHT = 600;

  private Stage primaryStage = null;
  private View view = null;

  public void setView(View v) {
    this.view = v;
    primaryStage.setScene(view.getScene());
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

    TfsDialog.loadAllDialogs(primaryStage);
    setView(new MainView());
  }
}
