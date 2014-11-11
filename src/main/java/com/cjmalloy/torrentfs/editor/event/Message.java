package com.cjmalloy.torrentfs.editor.event;

import com.cjmalloy.torrentfs.editor.core.Continuation;


public class Message
{
    public String msg;
    public Continuation ct = Continuation.NOP;

    public Message(String msg)
    {
        this.msg = msg;
    }

    public Message(String msg, Continuation ct)
    {
        this.msg = msg;
        this.ct = ct;
    }
}
