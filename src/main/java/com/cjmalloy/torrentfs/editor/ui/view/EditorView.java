package com.cjmalloy.torrentfs.editor.ui.view;

import java.awt.Component;

import javax.swing.JPanel;


public class EditorView implements View
{
    private JPanel layout;

    @Override
    public Component getLayout()
    {
        if (layout == null)
        {
            layout = new JPanel();
        }
        return layout;
    }

}
