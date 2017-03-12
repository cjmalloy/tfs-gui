package com.cjmalloy.torrentfs.editor.ui.swing.dialog;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.cjmalloy.torrentfs.editor.event.DoMessage;
import com.google.common.eventbus.Subscribe;


public class MessageDialog extends Dialog {
  public MessageDialog(JFrame parent) {
    super(parent);
  }

  @Subscribe
  public void setMessage(DoMessage event) {
    JOptionPane.showMessageDialog(parent, event.msg, "", JOptionPane.INFORMATION_MESSAGE);
    event.ct.next();
  }
}
