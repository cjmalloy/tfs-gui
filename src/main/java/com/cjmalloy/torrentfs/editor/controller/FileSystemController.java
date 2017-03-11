package com.cjmalloy.torrentfs.editor.controller;

import com.cjmalloy.torrentfs.editor.event.WorkspaceWatcher;
import com.cjmalloy.torrentfs.editor.model.FileSystemModel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;


public class FileSystemController extends Controller<FileSystemModel> {
  private static Preferences P = Preferences.userNodeForPackage(FileSystemController.class);

  private WorkspaceWatcher workspaceWatcher = new WorkspaceWatcher();

  public FileSystemController(FileSystemModel model) {
    this.model = model;

    String lastws = P.get("tfsLastWorkspace", null);
    setWorkspace(lastws == null ? null : Paths.get(lastws));
  }

  public void setWorkspace(Path workspace) {
    workspaceWatcher.setWorkspace(workspace);
    if (workspace == null) {
      P.remove("tfsLastWorkspace");
    } else {
      P.put("tfsLastWorkspace", workspace.toString());
    }
    model.workspace = workspace;
    update();
  }
}
