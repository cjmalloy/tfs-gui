package com.cjmalloy.torrentfs.editor.ui.view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.cjmalloy.torrentfs.editor.model.EditorModel;
import com.cjmalloy.torrentfs.editor.ui.component.FacetContainer;
import com.google.common.eventbus.Subscribe;


public class EditorView implements View
{
    private JTabbedPane tabs;
    private List<EditorFileModel> files = new ArrayList<>();
    private List<FacetContainer> fileEditors = new ArrayList<>();
    private int currentEditor = -1;

    public EditorView()
    {
        Controller.EVENT_BUS.register(this);
    }

    @Override
    public JTabbedPane getLayout()
    {
        if (tabs == null)
        {
            tabs = new JTabbedPane();
        }
        return tabs;
    }

    @Override
    public void onResize(Dimension dim)
    {
        getLayout().setSize(dim.width, dim.height);
    }

    @Subscribe
    public void update(EditorModel model)
    {
        for (EditorFileModel f : model.openFiles)
        {
            if (!files.contains(f))
            {
                FacetContainer e = new FacetContainer(f);
                files.add(f);
                fileEditors.add(e);
                getLayout().addTab(f.getTitle(), e.getLayout());
            }
        }
        for (int i=files.size()-1; i>=0; i--)
        {
            EditorFileModel f = files.get(i);
            if (!model.openFiles.contains(f))
            {
                FacetContainer e = fileEditors.get(i);
                e.close();
                fileEditors.remove(i);
                files.remove(i);
                getLayout().remove(e.getLayout());
            }
        }

        if (currentEditor != model.activeFile)
        {
            currentEditor = model.activeFile;
            getLayout().setSelectedIndex(currentEditor);
        }
    }

}
