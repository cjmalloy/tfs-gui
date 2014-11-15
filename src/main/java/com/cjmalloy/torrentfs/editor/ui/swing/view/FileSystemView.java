package com.cjmalloy.torrentfs.editor.ui.swing.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.model.FileSystemModel;
import com.cjmalloy.torrentfs.editor.ui.swing.model.FileTreeModel;
import com.google.common.eventbus.Subscribe;


public class FileSystemView implements View
{
    private JScrollPane widget;
    private JTree tree;

    private Path workspace = null;

    public FileSystemView()
    {
        Controller.EVENT_BUS.register(this);
    }

    @Override
    public JScrollPane getWidget()
    {
        if (widget == null)
        {
            widget = new JScrollPane(getTree());
        }
        return widget;
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

    private void edit(int row, Path path)
    {
        MainController.get().editor.openFile(path.toFile());
    }

    private JTree getTree()
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
                            showMenu(selRow, getPath(selPath));
                        }
                        else if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
                        {
                            edit(selRow, getPath(selPath));
                        }
                    }
                }
            });
        }
        return tree;
    }

    private void showMenu(int row, Path path)
    {
    }

    private static Path getPath(TreePath treePath)
    {
        Object[] pathElements = treePath.getPath();
        String[] ret = new String[pathElements.length-1];
        for (int i=0; i<ret.length; i++)
        {
            ret[i] = pathElements[i+1].toString();
        }
        return Paths.get(pathElements[0].toString(), ret);
    }

}
