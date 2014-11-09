package com.cjmalloy.torrentfs.editor.controller;

import com.google.common.eventbus.EventBus;


public class Controller<T>
{
    public static final EventBus EVENT_BUS = new EventBus();

    protected T model;

    public void update()
    {
        EVENT_BUS.post(model);
    }
}
