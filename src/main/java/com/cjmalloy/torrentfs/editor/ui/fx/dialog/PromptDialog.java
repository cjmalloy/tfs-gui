package com.cjmalloy.torrentfs.editor.ui.fx.dialog;

import javafx.stage.Window;
import javax.swing.JOptionPane;

import com.cjmalloy.torrentfs.editor.event.DoPrompt;
import com.cjmalloy.torrentfs.editor.ui.swing.dialog.Dialog;
import com.google.common.eventbus.Subscribe;


public class PromptDialog extends Dialog {
  public PromptDialog(Window parent) {
    super(parent);
  }

  @Subscribe
  public void setMessage(DoPrompt event) {
    int returnVal = JOptionPane.showConfirmDialog(parent, event.msg, "", JOptionPane.YES_NO_OPTION);
    switch (returnVal) {
      case JOptionPane.YES_OPTION:
        event.callback.onYes();
        break;
      case JOptionPane.NO_OPTION:
      case JOptionPane.CLOSED_OPTION:
        event.callback.onNo();
        break;
    }
  }
}
