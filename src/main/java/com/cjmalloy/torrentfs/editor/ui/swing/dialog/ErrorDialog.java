package com.cjmalloy.torrentfs.editor.ui.swing.dialog;

import com.cjmalloy.torrentfs.editor.event.DoErrorMessage;
import com.google.common.eventbus.Subscribe;

import javax.swing.*;


public class ErrorDialog extends Dialog {
  public ErrorDialog(JFrame parent) {
    super(parent);
  }

  @Subscribe
  public void setMessage(DoErrorMessage event) {
    JOptionPane.showMessageDialog(parent, event.msg, "", JOptionPane.ERROR_MESSAGE);
    event.ct.next();
  }
}
