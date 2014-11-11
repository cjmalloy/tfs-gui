package com.cjmalloy.torrentfs.editor.ui.dialog;

import java.awt.Frame;

import com.cjmalloy.torrentfs.editor.controller.Controller;


public abstract class Dialog
{
    protected Frame parent;

    public Dialog(Frame parent)
    {
        this.parent = parent;
        Controller.EVENT_BUS.register(this);
    }
}
