package com.cjmalloy.torrentfs.editor.ui.fx.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;

import com.cjmalloy.torrentfs.editor.event.DoErrorMessage;
import com.google.common.eventbus.Subscribe;


public class ErrorDialog extends TfsDialog {
  public ErrorDialog(Window parent) {
    super(parent);
  }

  @Subscribe
  public void setMessage(DoErrorMessage event) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setContentText(event.msg);

    alert.showAndWait();
    event.ct.next();
  }
}
