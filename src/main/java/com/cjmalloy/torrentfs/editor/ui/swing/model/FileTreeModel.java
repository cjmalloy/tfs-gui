package com.cjmalloy.torrentfs.editor.ui.swing.model;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.event.FileModificationEvent;
import com.google.common.eventbus.Subscribe;

/**
 * Model the file system as a tree.
 *
 * @author chris
 *
 */
public class FileTreeModel implements TreeModel
{
    private File root;

    private List<TreeModelListener> listeners = new ArrayList<>();

    public FileTreeModel(File rootDirectory)
    {
        root = rootDirectory;
        Controller.EVENT_BUS.register(this);
    }

    @Override
    public Object getRoot()
    {
        return root;
    }

    @Subscribe
    public void fileModified(FileModificationEvent event)
    {
        // TODO: this does not appear to work
        fireTreeNodesChanged(getPathForFile(event.file), null, null);
    }

    private TreePath getPathForFile(Path file)
    {
        return new TreePath(file);
    }

    @Override
    public Object getChild(Object parent, int index)
    {
        File directory = (File) parent;
        String[] children = directory.list();
        return new TreeFile(directory, children[index]);
    }

    @Override
    public int getChildCount(Object parent)
    {
        File file = (File) parent;
        if (file.isDirectory())
        {
            String[] fileList = file.list();
            if (fileList != null) return file.list().length;
        }
        return 0;
    }

    @Override
    public boolean isLeaf(Object node)
    {
        File file = (File) node;
        return file.isFile();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child)
    {
        File directory = (File) parent;
        File file = (File) child;
        String[] children = directory.list();
        for (int i = 0; i < children.length; i++)
        {
            if (file.getName().equals(children[i])) { return i; }
        }
        return -1;

    }

    @Override
    public void valueForPathChanged(TreePath path, Object value)
    {
        File oldFile = (File) path.getLastPathComponent();
        String fileParentPath = oldFile.getParent();
        String newFileName = (String) value;
        File targetFile = new File(fileParentPath, newFileName);
        oldFile.renameTo(targetFile);
        File parent = new File(fileParentPath);
        int[] changedChildrenIndices = { getIndexOfChild(parent, targetFile) };
        Object[] changedChildren = { targetFile };
        fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);

    }

    private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children)
    {
        TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
        for (TreeModelListener l : listeners)
        {
            l.treeNodesChanged(event);
        }
    }

    @Override
    public void addTreeModelListener(TreeModelListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listener)
    {
        listeners.remove(listener);
    }

    private class TreeFile extends File
    {
        private static final long serialVersionUID = 1L;

        public TreeFile(File parent, String child)
        {
            super(parent, child);
        }

        public String toString()
        {
            return getName();
        }
    }
}
