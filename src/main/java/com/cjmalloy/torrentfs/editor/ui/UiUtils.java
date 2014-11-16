package com.cjmalloy.torrentfs.editor.ui;


public abstract class UiUtils
{
    protected static UiUtils INSTANCE;

    public abstract void invokeLater(Runnable doRun);

    public static UiUtils get()
    {
        return INSTANCE;
    }
}
