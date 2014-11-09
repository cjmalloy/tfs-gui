package com.cjmalloy.torrentfs.editor.ui.component;

import java.awt.Component;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.EditorFileController;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.google.common.eventbus.Subscribe;


public class TextEditor implements FileEditorFacet
{

    private JTextArea layout;
    private EditorFileController controller;

    protected TextEditor(EditorFileController controller)
    {
        this.controller = controller;
        Controller.EVENT_BUS.register(this);
    }

    @Override
    public void close()
    {
        Controller.EVENT_BUS.unregister(this);
    }

    @Override
    public Component getLayout()
    {
        if (layout == null)
        {
            // TODO: switch to jsyntaxpane for syntax highlighting
            layout = new JTextArea();
            layout.getDocument().addDocumentListener(new DocumentListener()
            {
                @Override
                public void changedUpdate(DocumentEvent e)
                {
                    controller.setDirty();
                }

                @Override
                public void insertUpdate(DocumentEvent e)
                {
                    controller.setDirty();
                }

                @Override
                public void removeUpdate(DocumentEvent e)
                {
                    controller.setDirty();
                }
            });
        }
        return layout;
    }

    @Subscribe
    public void update(EditorFileModel model)
    {
        if (controller.model != model) return;


    }

}
