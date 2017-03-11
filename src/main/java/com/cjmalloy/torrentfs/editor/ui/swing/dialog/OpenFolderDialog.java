package com.cjmalloy.torrentfs.editor.ui.swing.dialog;

import com.cjmalloy.torrentfs.editor.event.DoOpenFolder;
import com.google.common.eventbus.Subscribe;

import javax.swing.*;
import java.io.File;


public class OpenFolderDialog extends Dialog {
  private JFileChooser chooser = new JFileChooser();

  public OpenFolderDialog(JFrame parent) {
    super(parent);

    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
  }

  @Subscribe
  public void open(DoOpenFolder event) {
    int returnVal = chooser.showOpenDialog(parent);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      event.callback.withFolder(file.toPath());
    }
  }
}
