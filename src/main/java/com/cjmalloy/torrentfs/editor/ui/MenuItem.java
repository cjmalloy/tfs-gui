package com.cjmalloy.torrentfs.editor.ui;

import com.cjmalloy.torrentfs.editor.core.Continuation;
import com.cjmalloy.torrentfs.editor.core.ExceptionHandler;
import com.cjmalloy.torrentfs.editor.core.ThrowsContinuation;


public class MenuItem {
  public String title;
  public ThrowsContinuation ct;
  public ExceptionHandler handler = ExceptionHandler.DEFAULT_HANDLER;

  public MenuItem() {
  }

  public MenuItem(String title, Continuation ct) {
    this.title = title;
    this.ct = ct;
  }

  public MenuItem(String title, ThrowsContinuation ct, ExceptionHandler handler) {
    this.title = title;
    this.ct = ct;
    this.handler = handler;
  }
}
