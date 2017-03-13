package com.cjmalloy.torrentfs.editor.ui.fx;

import javax.swing.SwingUtilities;

import com.cjmalloy.torrentfs.editor.ui.UiUtils;
import com.google.inject.Singleton;
import javafx.application.Platform;

@Singleton
public class FxUiUtils extends UiUtils {
  public FxUiUtils() {
    UiUtils.INSTANCE = this;
  }

  @Override
  public void invokeLater(Runnable doRun) {
    Platform.runLater(doRun);
  }
}
