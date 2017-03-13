package com.cjmalloy.torrentfs.editor.ui.fx.dialog;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import com.cjmalloy.torrentfs.editor.event.DoConfirm;
import com.google.common.eventbus.Subscribe;


public class ConfirmDialog extends TfsDialog {
  public ConfirmDialog(Window parent) {
    super(parent);
  }

  @Subscribe
  public void setMessage(DoConfirm event) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Current project is modified");
    alert.setContentText("Save?");
    ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
    ButtonType noButton = new ButtonType("No", ButtonData.NO);
    ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(okButton, noButton, cancelButton);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent()) {
      if (result.get() == okButton) {
        event.callback.onYes();
      } else {
        event.callback.onNo();
      }
    } else {
      event.callback.onCancel();
    }
  }
}
