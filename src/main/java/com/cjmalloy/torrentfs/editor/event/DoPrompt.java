package com.cjmalloy.torrentfs.editor.event;


public class DoPrompt {
  public String msg;
  public PromptCallback callback;

  public DoPrompt(String msg, PromptCallback callback) {
    this.msg = msg;
    this.callback = callback;
  }

  public interface PromptCallback {
    void onNo();

    void onYes();
  }
}
