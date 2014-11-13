package com.cjmalloy.torrentfs.editor.event;


public class YesNoCancelEvent
{
    public YesNoCancelCallback callback;

    public YesNoCancelEvent(String message, YesNoCancelCallback callback)
    {
        this.callback = callback;
    }

    public interface YesNoCancelCallback
    {
        void onCancel();
        void onNo();
        void onYes();
    }
}
