package com.cjmalloy.torrentfs.editor.ui.swing.dialog;

import com.cjmalloy.torrentfs.editor.event.DoConfirm;
import com.google.common.eventbus.Subscribe;

import javax.swing.*;


public class ConfirmDialog extends Dialog {
  public ConfirmDialog(JFrame parent) {
    super(parent);
  }

  @Subscribe
  public void setMessage(DoConfirm event) {
    int returnVal = JOptionPane.showConfirmDialog(parent, event.msg);
    switch (returnVal) {
      case JOptionPane.YES_OPTION:
        event.callback.onYes();
        break;
      case JOptionPane.NO_OPTION:
        event.callback.onNo();
        break;
      case JOptionPane.CANCEL_OPTION:
      case JOptionPane.CLOSED_OPTION:
        event.callback.onCancel();
        break;
    }
  }
}
