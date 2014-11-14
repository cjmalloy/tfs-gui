package com.cjmalloy.torrentfs.editor.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class EditorFileModel
{
    public File path;
    public byte[] contents;
    public boolean isTfs;
    public int editMode = 0;
    public List<Facet> supportedFacets = new ArrayList<>();
    public boolean editorModified = false;
    public boolean fileSystemModified = false;

    public EditorFileModel(File path)
    {
        this.path = path;
        isTfs = path.getName().equalsIgnoreCase(".tfs");
        if (isTfs) supportedFacets.add(Facet.RAW);
        supportedFacets.add(Facet.PROPERTIES);
    }

    public String getTitle()
    {
        if (isTfs)
        {
            return path.getParentFile().getName() + path.getName();
        }
        return path.getName();
    }

    public enum Facet
    {
        RAW,
        PROPERTIES;
    }
}
