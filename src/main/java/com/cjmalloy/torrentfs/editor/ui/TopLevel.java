package com.cjmalloy.torrentfs.editor.ui;


public interface TopLevel {
  /**
   * Exit the program when this container is closed.
   * <p>
   * This method will never return.
   */
  void exitOnFinish();
}
