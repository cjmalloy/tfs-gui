package com.cjmalloy.torrentfs.editor.controller;

import java.nio.file.Path;

import com.cjmalloy.torrentfs.editor.model.FileSystemModel;


public class FileSystemController extends Controller<FileSystemModel>
{

    public FileSystemController(FileSystemModel model)
    {
        this.model = model;
    }

    public void setWorkspace(Path workspace)
    {
        model.workspace = workspace;
        update();
    }
}
