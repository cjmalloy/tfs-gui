package com.cjmalloy.torrentfs.editor.ui.view;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
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

    @Override
    public JTree getLayout()
    {
        if (tree == null)
        {
            tree = new JTree();
            tree.addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    if (selRow != -1)
                    {
                        if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON2)
                        {
                            showMenu(selRow, selPath);
                        }
                        else if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
                        {
                            edit(selRow, selPath);
                        }
                    }
                }
            });
        }
        return tree;
    }

    @Override
    public void onResize(Dimension dim)
    {
        getLayout().setSize(dim.width, dim.height);
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

    private void edit(int row, TreePath path)
    {
        MainController.get().editor.openFile((File)path.getLastPathComponent());
    }

    private void showMenu(int row, TreePath path)
    {
    }

}
