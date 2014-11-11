package com.cjmalloy.torrentfs.editor.event;

import com.cjmalloy.torrentfs.editor.core.Continuation;


public class ErrorMessage
{
    public String msg;
    public Continuation ct = Continuation.NOP;

    public ErrorMessage(String msg)
    {
        this.msg = msg;
    }

    public ErrorMessage(String msg, Continuation ct)
    {
        this.msg = msg;
        this.ct = ct;
    }
}
