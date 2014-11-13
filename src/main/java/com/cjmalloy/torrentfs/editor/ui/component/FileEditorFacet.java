package com.cjmalloy.torrentfs.editor.ui.component;

import java.io.IOException;

import com.cjmalloy.torrentfs.editor.controller.EditorFileController;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel.EditFacet;
import com.cjmalloy.torrentfs.editor.ui.HasWidget;


public interface FileEditorFacet extends HasWidget
{
    void close();

    public static class FileEditorFactory
    {
        public static FileEditorFacet create(EditFacet facet, EditorFileController c) throws IOException
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
