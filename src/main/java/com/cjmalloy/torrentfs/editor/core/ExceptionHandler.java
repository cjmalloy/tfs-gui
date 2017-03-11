package com.cjmalloy.torrentfs.editor.core;


public interface ExceptionHandler {
  public ExceptionHandler DEFAULT_HANDLER = new ExceptionHandler() {
    @Override
    public void handleException(Exception e) {
      e.printStackTrace();
    }
  };

  void handleException(Exception e);
}
