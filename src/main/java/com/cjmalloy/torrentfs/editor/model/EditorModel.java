package com.cjmalloy.torrentfs.editor.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class EditorModel
{
    public List<EditorFileModel> openFiles = new ArrayList<>();
    public int activeFile = -1;

    public EditorFileModel get(File file)
    {
        for (EditorFileModel f : openFiles)
        {
            if (f.path == file) return f;
        }
        return null;
    }
}
