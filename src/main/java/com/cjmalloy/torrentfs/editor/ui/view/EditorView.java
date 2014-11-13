package com.cjmalloy.torrentfs.editor.ui.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.cjmalloy.torrentfs.editor.model.EditorModel;
import com.cjmalloy.torrentfs.editor.ui.component.ButtonTabComponent;
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
    public JTabbedPane getWidget()
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
        getWidget().setSize(dim.width, dim.height);
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
                getWidget().addTab(f.getTitle(), e.getWidget());
                final EditorFileModel _f = f;
                getWidget().setTabComponentAt(getWidget().getTabCount()-1, new ButtonTabComponent(getWidget(), new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent event)
                    {
                        MainController.get().editor.maybeClose(_f);
                    }
                }).getWidget());
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
                getWidget().remove(e.getWidget());
            }
        }

        if (currentEditor != model.activeFile)
        {
            currentEditor = model.activeFile;
            getWidget().setSelectedIndex(currentEditor);
        }
    }
}
