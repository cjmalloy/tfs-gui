package com.cjmalloy.torrentfs.editor.controller;

import com.google.common.eventbus.*;


public class Controller<T> {
  public static final EventBus EVENT_BUS = new EventBus(new SubscriberExceptionHandler() {
    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
      exception.printStackTrace();
    }
  });

  public T model;

  public Controller() {
    EVENT_BUS.register(this);
  }

  public void update() {
    EVENT_BUS.post(model);
  }

  public void updateAll() {
    update();
  }
}
