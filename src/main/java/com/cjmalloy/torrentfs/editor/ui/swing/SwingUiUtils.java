package com.cjmalloy.torrentfs.editor.ui.swing;

import javax.swing.SwingUtilities;

import com.cjmalloy.torrentfs.editor.ui.UiUtils;
import com.google.inject.Singleton;

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
