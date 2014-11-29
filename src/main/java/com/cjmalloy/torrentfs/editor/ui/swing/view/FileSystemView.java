package com.cjmalloy.torrentfs.editor.ui.swing.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.event.FileModificationEvent;
import com.cjmalloy.torrentfs.editor.model.FileSystemModel;
import com.cjmalloy.torrentfs.editor.ui.swing.component.PopupMenu;
import com.cjmalloy.torrentfs.editor.ui.swing.component.TfsTreeCellRenderer;
import com.cjmalloy.torrentfs.editor.ui.swing.model.FileTreeModel;
import com.google.common.eventbus.Subscribe;


public class FileSystemView implements View
{
    private JScrollPane widget;
    private JTree tree;

    private Path workspace = null;
    private FileTreeModel treeModel = null;
    private TreeCellRenderer cellRenderer = new TfsTreeCellRenderer();

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
    public void fileModified(FileModificationEvent event)
    {
        if (treeModel != null) treeModel.fileModified(event);
    }

    @Subscribe
    public void update(FileSystemModel model)
    {
        if (model.workspace != this.workspace)
        {
            if (model.workspace == null)
            {
                treeModel = null;
                getTree().setCellRenderer(null);
            }
            else
            {
                treeModel = new FileTreeModel(model.workspace.toFile());
                getTree().setCellRenderer(cellRenderer);
            }
            getTree().setModel(treeModel);
            this.workspace = model.workspace;
        }
    }

    protected void edit(Path path)
    {
        MainController.get().editor.openFile(path.toFile());
    }

    protected void showMenu(Path path, int x, int y)
    {
        new PopupMenu(MainController.get().getMenu(path.toFile())).show(getTree(), x, y);
    }

    private JTree getTree()
    {
        if (tree == null)
        {
            tree = new JTree();
            tree.setCellRenderer(null);
            tree.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mousePressed(MouseEvent e)
                {
                    doPopupTrigger(e);

                    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
                    {
                        int selRow = tree.getRowForLocation(e.getX(), e.getY());
                        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                        if (selRow != -1)
                        {
                            edit(getPath(selPath));
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e)
                {
                    doPopupTrigger(e);
                }

                private void doPopupTrigger(MouseEvent e)
                {
                    if (e.isPopupTrigger())
                    {
                        int selRow = tree.getRowForLocation(e.getX(), e.getY());
                        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                        if (selRow != -1)
                        {
                            showMenu(getPath(selPath), e.getX(), e.getY());
                        }
                    }
                }
            });
        }
        return tree;
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
