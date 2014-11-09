package com.cjmalloy.torrentfs.editor.ui.component;

import com.cjmalloy.torrentfs.editor.controller.EditorFileController;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel.EditFacet;
import com.cjmalloy.torrentfs.editor.ui.HasLayout;


public interface FileEditorFacet extends HasLayout
{
    void close();

    public static class FileEditorFactory
    {
        public static FileEditorFacet create(EditFacet facet, EditorFileController c)
        {
            switch (facet)
            {
            case PROPERTIES:
                return new PropertyEditor(c);
            case RAW:
                return new TextEditor(c);
            }
            return null;
        }
    }
}
