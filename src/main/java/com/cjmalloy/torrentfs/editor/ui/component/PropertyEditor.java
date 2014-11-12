package com.cjmalloy.torrentfs.editor.ui.component;

import java.awt.Component;

import javax.swing.JPanel;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.EditorFileController;
import com.cjmalloy.torrentfs.editor.model.EditorModel;
import com.google.common.eventbus.Subscribe;


public class PropertyEditor implements FileEditorFacet
{
    private JPanel widget;

    private EditorFileController controller;

    public PropertyEditor(EditorFileController controller)
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
    public Component getWidget()
    {
        if (widget == null)
        {
            widget = new JPanel();
        }
        return widget;
    }

    @Subscribe
    public void update(EditorModel model)
    {

    }

}
