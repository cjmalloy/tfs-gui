package com.cjmalloy.torrentfs.editor.ui.swing;

import com.cjmalloy.torrentfs.editor.ui.UiUtils;
import com.google.inject.Singleton;

import javax.swing.*;

@Singleton
public class SwingUiUtils extends UiUtils {
  public SwingUiUtils() {
    UiUtils.INSTANCE = this;
  }

  @Override
  public void invokeLater(Runnable doRun) {
    SwingUtilities.invokeLater(doRun);
  }
}
