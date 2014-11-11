package com.cjmalloy.torrentfs.editor.core;


public interface Continuation
{
    public static final Continuation NOP = new Continuation(){public void next(){}};

    void next();
}
