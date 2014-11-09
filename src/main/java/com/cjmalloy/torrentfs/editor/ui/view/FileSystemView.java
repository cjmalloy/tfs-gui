package com.cjmalloy.torrentfs.editor.ui.view;

import java.awt.Component;

import javax.swing.JTree;


public class FileSystemView implements View
{
    private JTree tree;

    @Override
    public Component getLayout()
    {
        if (tree == null)
        {
            tree = new JTree();
        }
        return tree;
    }

}
