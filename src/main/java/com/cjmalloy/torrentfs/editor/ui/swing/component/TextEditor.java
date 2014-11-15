package com.cjmalloy.torrentfs.editor.ui.swing.component;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.EditorFileController;
import com.cjmalloy.torrentfs.editor.event.DoFlushFile;
import com.cjmalloy.torrentfs.editor.event.RefreshFileEvent;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.google.common.eventbus.Subscribe;


public class TextEditor implements FacetEditor
{

    private JScrollPane widget;
    private JTextArea textArea;
    private EditorFileController controller;

    private boolean checkDirty = true;

    protected TextEditor(EditorFileController controller) throws IOException
    {
        this.controller = controller;
        Controller.EVENT_BUS.register(this);
        controller.loadAndRefresh();
    }

    @Override
    public void close()
    {
        Controller.EVENT_BUS.unregister(this);
    }

    @Override
    public JScrollPane getWidget()
    {
        if (widget == null)
        {
            widget = new JScrollPane(getTextArea());
        }
        return widget;
    }

    @Subscribe
    public void onDoFlushFile(DoFlushFile event)
    {
        if (controller.model != event.file) return;

        controller.flush(getTextArea().getText());
    }

    @Subscribe
    public void onRefreshFile(RefreshFileEvent event)
    {
        if (controller.model != event.file) return;

        checkDirty = false;
        getTextArea().setText(new String(event.file.contents, Charset.forName("UTF-8")));
        getTextArea().setCaretPosition(0);
        checkDirty = true;
    }

    @Subscribe
    public void update(EditorFileModel model)
    {
        if (controller.model != model) return;


    }

    private JTextArea getTextArea()
    {
        if (textArea == null)
        {
            // TODO: switch to jsyntaxpane for syntax highlighting
            textArea = new JTextArea();
            textArea.getDocument().addDocumentListener(new DocumentListener()
            {
                @Override
                public void changedUpdate(DocumentEvent e)
                {
                    setDirty();
                }

                @Override
                public void insertUpdate(DocumentEvent e)
                {
                    setDirty();
                }

                @Override
                public void removeUpdate(DocumentEvent e)
                {
                    setDirty();
                }
            });
        }
        return textArea;
    }

    private void setDirty()
    {
        if (!checkDirty) return;
        controller.setDirty();
    }

}
