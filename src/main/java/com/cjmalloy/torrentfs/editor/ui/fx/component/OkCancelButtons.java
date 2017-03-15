package com.cjmalloy.torrentfs.editor.ui.fx.component;

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
  protected ButtonModel[] getButtons() {
    // TODO: localize
    return new ButtonModel[] {
        new ButtonModel("OK", new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            delegate.onOk();
          }
        }),
        new ButtonModel("Cancel", new ActionListener() {
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
