package com.cjmalloy.torrentfs.editor.event;

import com.cjmalloy.torrentfs.editor.model.ExportSettings;


public class DoExport
{
    public ExportCallback callback;

    public DoExport(ExportCallback callback)
    {
        this.callback = callback;
    }

    public interface ExportCallback
    {
        void withSettings(ExportSettings settings);
    }
}
