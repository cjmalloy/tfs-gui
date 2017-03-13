package com.cjmalloy.torrentfs.editor.ui.fx.dialog;

import javafx.stage.Window;
import javax.swing.JOptionPane;

import com.cjmalloy.torrentfs.editor.event.DoErrorMessage;
import com.cjmalloy.torrentfs.editor.ui.swing.dialog.Dialog;
import com.google.common.eventbus.Subscribe;


public class ErrorDialog extends Dialog {
  public ErrorDialog(Window parent) {
    super(parent);
  }

  @Subscribe
  public void setMessage(DoErrorMessage event) {
    JOptionPane.showMessageDialog(parent, event.msg, "", JOptionPane.ERROR_MESSAGE);
    event.ct.next();
  }
}
