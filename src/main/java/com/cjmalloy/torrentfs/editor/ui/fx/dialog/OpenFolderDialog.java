package com.cjmalloy.torrentfs.editor.ui.fx.dialog;

import java.io.File;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import com.cjmalloy.torrentfs.editor.event.DoOpenFolder;
import com.google.common.eventbus.Subscribe;


public class OpenFolderDialog extends TfsDialog {
  private DirectoryChooser chooser = new DirectoryChooser();

  public OpenFolderDialog(Window parent) {
    super(parent);
  }

  @Subscribe
  public void open(DoOpenFolder event) {
    File returnVal = chooser.showDialog(parent);
    if (returnVal != null) {
      event.callback.withFolder(returnVal.toPath());
    }
  }
}
