package com.cjmalloy.torrentfs.editor.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class EditorFileModel
{
    public File path;
    public boolean isTfs;
    public int editMode = 0;
    public List<EditFacet> supportedFacets = new ArrayList<>();
    public boolean dirty = false;
    public boolean flush = false;

    public EditorFileModel(File path)
    {
        this.path = path;
        isTfs = path.getName().equalsIgnoreCase(".tfs");
        if (isTfs) supportedFacets.add(EditFacet.RAW);
        supportedFacets.add(EditFacet.PROPERTIES);
    }

    public String getTitle()
    {
        if (isTfs)
        {
            return path.getParentFile().getName() + path.getName();
        }
        return path.getName();
    }

    public enum EditFacet
    {
        RAW,
        PROPERTIES;
    }
}
