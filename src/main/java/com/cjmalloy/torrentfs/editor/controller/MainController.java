package com.cjmalloy.torrentfs.editor.controller;

import com.cjmalloy.torrentfs.editor.model.document.MainDocument;


public class MainController extends Controller<MainDocument>
{

    private static MainController instance;

    public FileSystemController fileSystem;
    public EditorController editor;

    private MainController()
    {
        model = new MainDocument();

        fileSystem = new FileSystemController(model.fileSystemModel);
        editor = new EditorController(model.editorModel);
    }

    public void export()
    {
        // TODO Auto-generated method stub

    }

    public boolean hasUnsavedChanges()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void open()
    {
        // TODO Auto-generated method stub

    }

    public void requestExit()
    {
        if (!hasUnsavedChanges())
        {
            model.exitNow = true;
            update();
        }
        else
        {
            showSaveExitCancelDialog();
        }
    }

    public void showSaveExitCancelDialog()
    {
        // TODO Auto-generated method stub

    }

    public static MainController get()
    {
        if (instance == null)
        {
            instance = new MainController();
        }
        return instance;
    }
}
