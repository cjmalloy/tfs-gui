package com.cjmalloy.torrentfs.editor.ui.dialog;

import javax.swing.JFrame;

import com.cjmalloy.torrentfs.editor.controller.Controller;


public abstract class Dialog
{
    protected JFrame parent;

    public Dialog(JFrame parent)
    {
        this.parent = parent;
        Controller.EVENT_BUS.register(this);
    }
}
