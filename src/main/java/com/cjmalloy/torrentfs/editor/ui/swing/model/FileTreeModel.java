package com.cjmalloy.torrentfs.editor.ui.swing.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public void addTreeModelListener(TreeModelListener listener)
    {
        listeners.add(listener);
    }

    @Subscribe
    public void fileModified(FileModificationEvent event)
    {
        switch (event.type)
        {
        case CREATE:
            fireTreeNodesInserted(event.file.toFile());
            break;
        case DELETE:
            fireTreeNodesDeleted(event.file.toFile());
            break;
        case MODIFY:
            fireTreeNodesChanged(event.file.toFile());
            break;
        }
    }

    @Override
    public Object getChild(Object parent, int index)
    {
        File directory = (File) parent;
        return listDirectory(directory)[index];
    }

    @Override
    public int getChildCount(Object parent)
    {
        File file = (File) parent;
        if (file.isDirectory())
        {
            String[] fileList = file.list();
            if (fileList != null) return fileList.length;
        }
        return 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child)
    {
        File directory = (File) parent;
        File file = (File) child;
        TreeFile[] children = listDirectory(directory);
        for (int i = 0; i < children.length; i++)
        {
            if (file.equals(children[i])) { return i; }
        }
        return -1;
    }

    @Override
    public Object getRoot()
    {
        return root;
    }

    @Override
    public boolean isLeaf(Object node)
    {
        File file = (File) node;
        return file.isFile();
    }

    public TreeFile[] listDirectory(File dir)
    {
        File[] children = dir.listFiles();
        TreeFile[] sorted = new TreeFile[children.length];
        for (int i=0; i<children.length; i++)
        {
            sorted[i] = new TreeFile(children[i]);
        }
        Arrays.sort(sorted);
        return sorted;
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listener)
    {
        listeners.remove(listener);
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

    private void fireTreeNodesChanged(File file)
    {
        fireTreeNodesChanged(getPathForFile(file));
    }

    private void fireTreeNodesChanged(TreePath parentPath)
    {
        TreeModelEvent event = new TreeModelEvent(this, parentPath);
        for (TreeModelListener l : listeners)
        {
            l.treeNodesChanged(event);
        }
    }

    private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children)
    {
        TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
        for (TreeModelListener l : listeners)
        {
            l.treeNodesChanged(event);
        }
    }

    private void fireTreeNodesDeleted(File file)
    {
        File parentFile = file.getParentFile();
        TreePath parentPath = getPathForFile(parentFile);
        int[] indices = {getDeletedIndex(parentFile, file)};
        Object[] children = {new TreeFile(file)};
        TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
        for (TreeModelListener l : listeners)
        {
            l.treeNodesRemoved(event);
        }
    }

    private void fireTreeNodesInserted(File file)
    {
        File parentFile = file.getParentFile();
        TreePath parentPath = getPathForFile(parentFile);
        int[] indices = {getIndexOfChild(parentFile, file)};
        Object[] children = {new TreeFile(file)};
        TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
        for (TreeModelListener l : listeners)
        {
            l.treeNodesInserted(event);
        }
    }

    private int getDeletedIndex(File directory, File file)
    {
        List<String> children = new ArrayList<>(Arrays.asList(directory.list()));
        children.add(file.getName());
        Collections.sort(children);
        for (int i = 0; i < children.size(); i++)
        {
            if (file.getName().equals(children.get(i))) { return i; }
        }
        return -1;
    }

    private TreePath getPathForFile(File file)
    {
        List<File> path = new ArrayList<>();
        while (!file.equals(root))
        {
            path.add(file);
            file = file.getParentFile();
        }
        path.add(root);
        Collections.reverse(path);
        return new TreePath(path.toArray());
    }

    public class TreeFile extends File
    {
        private static final long serialVersionUID = 1L;

        public TreeFile(File f)
        {
            super(f.toString());
        }

        public TreeFile(File parent, String child)
        {
            super(parent, child);
        }

        @Override
        public int compareTo(File pathname)
        {
            if (getName().endsWith(".tfs")) return -1;
            if (pathname.getName().endsWith(".tfs")) return 1;
            if (isDirectory() == pathname.isDirectory()) return super.compareTo(pathname);
            return isDirectory() ? -1 : 1;
        }

        public String toString()
        {
            return getName();
        }
    }
}
