package com.cjmalloy.torrentfs.editor.controller;

import com.google.common.eventbus.EventBus;


public class Controller<T>
{
    public static final EventBus EVENT_BUS = new EventBus();

    public T model;

    public Controller()
    {
        EVENT_BUS.register(this);
    }

    public void update()
    {
        EVENT_BUS.post(model);
    }

    public void updateAll()
    {
        update();
    }
}
