package com.cjmalloy.torrentfs.editor.ui.component;

import java.awt.Component;

import javax.swing.JPanel;

import com.cjmalloy.torrentfs.editor.model.ExportSettings;
import com.cjmalloy.torrentfs.editor.ui.HasLayout;


public class ExportSettingsComponent implements HasLayout
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

    public ExportSettings getValue()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
