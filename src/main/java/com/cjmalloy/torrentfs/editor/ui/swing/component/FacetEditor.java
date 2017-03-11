package com.cjmalloy.torrentfs.editor.ui.swing.component;

import com.cjmalloy.torrentfs.editor.controller.EditorFileController;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel.Facet;
import com.cjmalloy.torrentfs.editor.ui.swing.HasWidget;

import java.io.IOException;


public interface FacetEditor extends HasWidget {
  void close();

  public static class FileEditorFactory {
    public static FacetEditor create(Facet facet, EditorFileController c) throws IOException {
      switch (facet) {
        case PROPERTIES:
          return new PropertyEditor(c);
        case RAW:
          return new TextEditor(c);
      }
      return null;
    }
  }
}
