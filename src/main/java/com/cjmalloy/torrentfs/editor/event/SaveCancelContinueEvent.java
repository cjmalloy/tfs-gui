package com.cjmalloy.torrentfs.editor.event;


public class SaveCancelContinueEvent
{
    public SaveCancelContinueCallback callback;

    public SaveCancelContinueEvent(SaveCancelContinueCallback callback)
    {
        this.callback = callback;
    }

    public interface SaveCancelContinueCallback
    {
        void onSave();
        void onCancel();
        void onContinue();
    }
}
