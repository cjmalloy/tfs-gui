package com.cjmalloy.torrentfs.editor.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.cjmalloy.torrentfs.editor.model.EditorModel;


public class EditorController extends Controller<EditorModel>
{
    public List<EditorFileController> fileControllers = new ArrayList<>();

    public EditorController(EditorModel model)
    {
        this.model = model;
    }

    public void closeAll()
    {
        fileControllers.clear();
        model.openFiles.clear();
        update();
    }

    public EditorFileController getController(EditorFileModel f)
    {
        for (EditorFileController c : fileControllers)
        {
            if (c.model == f) return c;
        }
        return null;
    }

    public boolean hasUnsavedChanges()
    {
        for (EditorFileController c : fileControllers)
        {
            if (c.hasUnsavedChanges()) return true;
        }
        return false;
    }

    public void openFile(File file)
    {
        EditorFileModel editorFile = model.get(file);
        if (editorFile == null)
        {
            editorFile = new EditorFileModel(file);
            model.openFiles.add(editorFile);
        }
        switchTo(editorFile);
    }

    public void saveAll()
    {
        for (EditorFileController c : fileControllers)
        {
            c.save();
        }
    }

    public void switchTo(EditorFileModel file)
    {
        model.activeFile = model.openFiles.indexOf(file);
        update();
    }
}
