package com.cjmalloy.torrentfs.editor.controller;

import com.cjmalloy.torrentfs.editor.model.EditorFileModel;


public class EditorFileController extends Controller<EditorFileModel>
{

    public EditorFileController(EditorFileModel model)
    {
        this.model = model;
    }

    public boolean hasUnsavedChanges()
    {
        return model.dirty;
    }

    public void save()
    {
        if (!model.dirty) return;
        model.flush = true;
        update();
    }

    public void setDirty()
    {
        model.dirty = true;
    }

}
