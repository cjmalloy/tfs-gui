package com.cjmalloy.torrentfs.editor.ui.swing.component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class OkCancelButtons extends Buttons {
  private OkCancelDelegate delegate;

  public OkCancelButtons() {
  }

  public OkCancelButtons(OkCancelDelegate delegate) {
    setDelegate(delegate);
  }

  public void setDelegate(OkCancelDelegate delegate) {
    this.delegate = delegate;
  }

  @Override
  protected Button[] getButtons() {
    return new Button[]
      {
        new Button(UIManager.getString("OptionPane.okButtonText"), new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            delegate.onOk();
          }
        }),
        new Button(UIManager.getString("OptionPane.cancelButtonText"), new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            delegate.onCancel();
          }
        })
      };
  }

  public static interface OkCancelDelegate {
    void onCancel();

    void onOk();
  }

}
