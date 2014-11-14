package com.cjmalloy.torrentfs.editor.event;

import com.cjmalloy.torrentfs.editor.model.EditorFileModel;


public class RefreshFileEvent
{
    public EditorFileModel file;

    public RefreshFileEvent(EditorFileModel file)
    {
        this.file = file;
    }
}
