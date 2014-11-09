package com.cjmalloy.torrentfs.editor.event;

import com.cjmalloy.torrentfs.editor.model.ExportSettings;


public class ExportEvent
{
    public ExportCallback callback;

    public ExportEvent(ExportCallback callback)
    {
        this.callback = callback;
    }

    public interface ExportCallback
    {
        void withSettings(ExportSettings settings);
    }
}
