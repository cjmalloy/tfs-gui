package com.cjmalloy.torrentfs.editor.event;

import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.google.common.eventbus.Subscribe;


public class FlushFileEvent {
  public EditorFileModel file;

  public FlushFileEvent(EditorFileModel file) {
    this.file = file;
  }

  public interface FlushListener {
    @Subscribe
    void onFlush(FlushFileEvent event);
  }
}
