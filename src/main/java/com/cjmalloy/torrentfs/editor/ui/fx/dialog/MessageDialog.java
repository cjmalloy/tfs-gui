package com.cjmalloy.torrentfs.editor.ui.fx.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;

import com.cjmalloy.torrentfs.editor.event.DoMessage;
import com.google.common.eventbus.Subscribe;


public class MessageDialog extends TfsDialog {
  public MessageDialog(Window parent) {
    super(parent);
  }

  @Subscribe
  public void setMessage(DoMessage event) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setContentText(event.msg);
    alert.showAndWait();
    event.ct.next();
  }
}
