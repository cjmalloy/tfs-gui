package com.cjmalloy.torrentfs.editor.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import com.cjmalloy.torrentfs.editor.event.WorkspaceWatcher;
import com.cjmalloy.torrentfs.editor.model.FileSystemModel;


public class FileSystemController extends Controller<FileSystemModel>
{
    private static Preferences P = Preferences.userNodeForPackage(FileSystemController.class);

    private WorkspaceWatcher workspaceWatcher = new WorkspaceWatcher();

    public FileSystemController(FileSystemModel model)
    {
        this.model = model;

        setWorkspace(Paths.get(P.get("tfsLastWorkspace", System.getProperty("user.home"))));
    }

    public void setWorkspace(Path workspace)
    {
        workspaceWatcher.setWorkspace(workspace);
        P.put("tfsLastWorkspace", workspace.toString());
        model.workspace = workspace;
        update();
    }
}
