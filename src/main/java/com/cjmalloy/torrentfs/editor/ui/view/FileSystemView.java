package com.cjmalloy.torrentfs.editor.ui.view;

import java.awt.Dimension;
import java.nio.file.Path;

import javax.swing.JTree;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.model.FileSystemModel;
import com.cjmalloy.torrentfs.editor.ui.model.FileTreeModel;
import com.google.common.eventbus.Subscribe;


public class FileSystemView implements View
{
    private JTree tree;

    private Path workspace = null;

    public FileSystemView()
    {
        Controller.EVENT_BUS.register(this);
    }

    @Subscribe
    public void update(FileSystemModel model)
    {
        if (model.workspace != this.workspace)
        {
            try
            {
            if (model.workspace == null)
            {
                tree.setModel(null);
            }
            else
            {
                tree.setModel(new FileTreeModel(model.workspace.toFile()));
            }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
            this.workspace  = model.workspace;
        }
    }

    @Override
    public JTree getLayout()
    {
        if (tree == null)
        {
            tree = new JTree();
        }
        return tree;
    }

    @Override
    public void onResize(Dimension dim)
    {
        getLayout().setSize(dim.width, dim.height);
    }

}
