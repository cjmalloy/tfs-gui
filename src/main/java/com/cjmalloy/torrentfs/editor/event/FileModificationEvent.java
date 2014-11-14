package com.cjmalloy.torrentfs.editor.event;

import java.nio.file.Path;


public class FileModificationEvent
{
    public Path file;

    public FileModificationEvent(Path file)
    {
        this.file = file;
    }
}
