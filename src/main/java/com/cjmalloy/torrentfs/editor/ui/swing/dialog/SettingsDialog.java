package com.cjmalloy.torrentfs.editor.ui.swing.dialog;

import com.cjmalloy.torrentfs.editor.ui.swing.component.OkCancelButtons;
import com.cjmalloy.torrentfs.editor.ui.swing.component.OkCancelButtons.OkCancelDelegate;
import com.cjmalloy.torrentfs.editor.ui.swing.component.SettingsComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class SettingsDialog<T> {
  /**
   * Return value from class method if CANCEL is chosen.
   */
  public static final int CANCEL_OPTION = 2;

  /**
   * Return value form class method if OK is chosen.
   */
  public static final int OK_OPTION = 0;

  /**
   * Return value from class method if user closes window without selecting
   * anything, more than likely this should be treated as either a
   * <code>CANCEL_OPTION</code> or <code>NO_OPTION</code>.
   */
  public static final int CLOSED_OPTION = -1;

  private Frame parent;
  private JDialog dialog;
  private JPanel layout;
  private SettingsComponent<T> child;
  private OkCancelButtons okCancelButtons;

  private int lastResponse;

  public SettingsDialog(Frame parent, String title, SettingsComponent<T> child) {
    this.parent = parent;
    dialog = new JDialog(parent, title, true);
    dialog.add(getLayout(child.getWidget()));
    dialog.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        lastResponse = CLOSED_OPTION;
      }
    });
  }

  public T getValue() {
    return child.getValue();
  }

  public int showSettingsDialog() {
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
    return lastResponse;
  }

  private OkCancelButtons getButtons() {
    if (okCancelButtons == null) {
      okCancelButtons = new OkCancelButtons();
      okCancelButtons.setDelegate(new OkCancelDelegate() {
        @Override
        public void onCancel() {
          lastResponse = CANCEL_OPTION;
          dialog.setVisible(false);
        }

        @Override
        public void onOk() {
          lastResponse = OK_OPTION;
          dialog.setVisible(false);
        }
      });
    }
    return okCancelButtons;
  }

  private JPanel getLayout(Component child) {
    if (layout == null) {
      layout = new JPanel();
      layout.setLayout(new BoxLayout(layout, BoxLayout.PAGE_AXIS));
      layout.add(child);
      layout.add(getButtons().getWidget());
    }
    return layout;
  }

}
