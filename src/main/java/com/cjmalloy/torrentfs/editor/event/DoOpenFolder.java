package com.cjmalloy.torrentfs.editor.event;

import java.nio.file.Path;


public class DoOpenFolder {
  public OpenFolderCallback callback;

  public DoOpenFolder(OpenFolderCallback callback) {
    this.callback = callback;
  }

  public interface OpenFolderCallback {
    void withFolder(Path folder);
  }
}
