package com.cjmalloy.torrentfs.editor.event;

import java.nio.file.Path;


public class FileModificationEvent {
  public Path file;
  public FileModification type;

  public FileModificationEvent(Path file, FileModification type) {
    this.file = file;
    this.type = type;
  }

  public enum FileModification {
    CREATE,
    DELETE,
    MODIFY;
  }
}
