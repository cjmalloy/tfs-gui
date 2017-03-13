package com.cjmalloy.torrentfs.editor.ui.fx.dialog;

import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Window;

import com.cjmalloy.torrentfs.editor.event.*;
import com.google.common.eventbus.Subscribe;


public class ProgressDialog extends TfsDialog {
  private Dialog dialog;
  private ProgressIndicator progressIndicator;

  public ProgressDialog(Window parent) {
    super(parent);
  }

  @Subscribe
  public void onStart(ProgressStartEvent event) {
    dialog = new Dialog();
    progressIndicator = new ProgressIndicator();
    dialog.getDialogPane().setContent(progressIndicator);
    dialog.show();
  }

  @Subscribe
  public void onUpdate(ProgressUpdateEvent event) {
    progressIndicator.setProgress(event.p);
  }

  @Subscribe
  public void onEnd(ProgressEndEvent event) {
    dialog.close();
    dialog = null;
  }
}
