package com.cjmalloy.torrentfs.editor.ui.fx;

import javafx.application.Platform;

import com.cjmalloy.torrentfs.editor.ui.UiUtils;
import com.google.inject.Singleton;

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
