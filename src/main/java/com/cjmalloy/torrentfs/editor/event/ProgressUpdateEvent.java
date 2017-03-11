package com.cjmalloy.torrentfs.editor.event;


public class ProgressUpdateEvent {
  public double p;

  public ProgressUpdateEvent(double p) {
    this.p = Math.min(1, Math.max(0, p));
  }
}
