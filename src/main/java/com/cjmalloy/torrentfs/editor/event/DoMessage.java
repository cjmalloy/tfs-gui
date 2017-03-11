package com.cjmalloy.torrentfs.editor.event;

import com.cjmalloy.torrentfs.editor.core.Continuation;


public class DoMessage {
  public String msg;
  public Continuation ct = Continuation.NOP;

  public DoMessage(String msg) {
    this.msg = msg;
  }

  public DoMessage(String msg, Continuation ct) {
    this.msg = msg;
    this.ct = ct;
  }
}
