package com.cjmalloy.torrentfs.editor.ui.fx.dialog;

import javafx.stage.Window;

import com.cjmalloy.torrentfs.editor.controller.Controller;


public abstract class Dialog {
  protected Window parent;

  public Dialog(Window parent) {
    this.parent = parent;
    Controller.EVENT_BUS.register(this);
  }

  public static void loadAllDialogs(Window parent) {
    new OpenFolderDialog(parent);
    new ExportDialog(parent);
    new MessageDialog(parent);
    new ErrorDialog(parent);
    new ConfirmDialog(parent);
    new PromptDialog(parent);
    new ProgressDialog(parent);
  }
}
