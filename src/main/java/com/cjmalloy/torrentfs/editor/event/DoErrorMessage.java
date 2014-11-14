package com.cjmalloy.torrentfs.editor.event;

import com.cjmalloy.torrentfs.editor.core.Continuation;


public class DoErrorMessage
{
    public String msg;
    public Continuation ct = Continuation.NOP;

    public DoErrorMessage(String msg)
    {
        this.msg = msg;
    }

    public DoErrorMessage(String msg, Continuation ct)
    {
        this.msg = msg;
        this.ct = ct;
    }
}
