package com.cjmalloy.torrentfs.editor.event;


public class ProgressUpdate
{
    public double p;

    public ProgressUpdate(double p)
    {
        this.p = Math.min(1, Math.max(0, p));
    }
}
