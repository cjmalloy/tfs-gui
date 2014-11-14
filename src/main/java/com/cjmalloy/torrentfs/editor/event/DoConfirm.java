package com.cjmalloy.torrentfs.editor.event;


public class DoConfirm
{
    public String msg;
    public ConfirmCallback callback;

    public DoConfirm(String msg, ConfirmCallback callback)
    {
        this.msg = msg;
        this.callback = callback;
    }

    public interface ConfirmCallback
    {
        void onCancel();
        void onNo();
        void onYes();
    }
}
