package com.cjmalloy.torrentfs.editor.ui.swing.skin;

import javax.swing.ImageIcon;


public class IconBundle
{
    private static IconBundle instance = null;

    private ImageIcon exportIcon = new ImageIcon(IconBundle.class.getResource("icons/document-save.png"));
    private ImageIcon openIcon = new ImageIcon(IconBundle.class.getResource("icons/document-open.png"));

    private IconBundle() {}

    public ImageIcon getExportIcon()
    {
        return exportIcon;
    }

    public ImageIcon getOpenIcon()
    {
        return openIcon;
    }

    public static IconBundle get()
    {
        if (instance == null)
        {
            instance = new IconBundle();
        }
        return instance ;
    }
}
