package com.cjmalloy.torrentfs.editor.ui.dialog;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.cjmalloy.torrentfs.editor.event.OpenFolderEvent;
import com.google.common.eventbus.Subscribe;


public class OpenFolderDialog extends Dialog
{
    private JFileChooser chooser = new JFileChooser();

    public OpenFolderDialog(JFrame parent)
    {
        super(parent);

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    @Subscribe
    public void open(OpenFolderEvent event)
    {
        int returnVal = chooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile();
            event.callback.withFolder(file.toPath());
        }
    }
}
