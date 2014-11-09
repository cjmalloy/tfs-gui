package com.cjmalloy.torrentfs.editor.event;

import java.nio.file.Path;


public class OpenFolderEvent
{
    public OpenFolderCallback callback;

    public OpenFolderEvent(OpenFolderCallback callback)
    {
        this.callback = callback;
    }

    public interface OpenFolderCallback
    {
        void withFolder(Path folder);
    }
}
