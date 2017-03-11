package com.cjmalloy.torrentfs.editor.model.document;

import com.cjmalloy.torrentfs.editor.model.EditorModel;
import com.cjmalloy.torrentfs.editor.model.FileSystemModel;
import com.cjmalloy.torrentfs.model.Meta;


public class MainDocument {
  public FileSystemModel fileSystemModel = new FileSystemModel();
  public EditorModel editorModel = new EditorModel();

  public Meta metadata = new Meta();
}
