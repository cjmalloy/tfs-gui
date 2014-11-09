package com.cjmalloy.torrentfs.editor.ui.view;

import java.awt.Dimension;

import javax.swing.JTree;


public class FileSystemView implements View
{
    private JTree tree;

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
