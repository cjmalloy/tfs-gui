package com.cjmalloy.torrentfs.editor.ui.swing.dialog;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.cjmalloy.torrentfs.editor.event.DoErrorMessage;
import com.google.common.eventbus.Subscribe;


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
