package com.cjmalloy.torrentfs.editor.event;

import com.cjmalloy.torrentfs.editor.model.EditorFileModel;

/**
 * Request that this file be flushed to disk.
 *
 * @author chris
 */
public class DoFlushFile {
  public EditorFileModel file;

  public DoFlushFile(EditorFileModel file) {
    this.file = file;
  }
}
