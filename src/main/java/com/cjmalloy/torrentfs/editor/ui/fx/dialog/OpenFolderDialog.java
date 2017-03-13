package com.cjmalloy.torrentfs.editor.ui.fx.dialog;

import java.io.File;
import javafx.stage.Window;
import javax.swing.JFileChooser;

import com.cjmalloy.torrentfs.editor.event.DoOpenFolder;
import com.cjmalloy.torrentfs.editor.ui.swing.dialog.Dialog;
import com.google.common.eventbus.Subscribe;


public class OpenFolderDialog extends Dialog {
  private JFileChooser chooser = new JFileChooser();

  public OpenFolderDialog(Window parent) {
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
