package com.cjmalloy.torrentfs.editor.event;

import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.google.common.eventbus.Subscribe;


public class FlushEvent
{
    public EditorFileModel file;

    public FlushEvent(EditorFileModel file)
    {
        this.file = file;
    }

    public interface FlushListener
    {
        @Subscribe
        void onFlush(FlushEvent event);
    }
}
