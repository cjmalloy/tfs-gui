package com.cjmalloy.torrentfs.editor.ui;

import com.cjmalloy.torrentfs.editor.core.Continuation;


public class MenuItem
{
    public String title;
    public Continuation ct;

    public MenuItem() {}

    public MenuItem(String title, Continuation ct)
    {
        this.title = title;
        this.ct = ct;
    }
}
