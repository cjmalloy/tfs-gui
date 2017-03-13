package com.cjmalloy.torrentfs.editor.ui.fx.dialog;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import com.cjmalloy.torrentfs.editor.event.DoPrompt;
import com.google.common.eventbus.Subscribe;


public class PromptDialog extends TfsDialog {
  public PromptDialog(Window parent) {
    super(parent);
  }

  @Subscribe
  public void setMessage(DoPrompt event) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setContentText(event.msg);
    ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
    ButtonType noButton = new ButtonType("No", ButtonData.NO);
    alert.getButtonTypes().setAll(okButton, noButton);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == okButton) {
      event.callback.onYes();
    } else {
      event.callback.onNo();
    }
  }
}
