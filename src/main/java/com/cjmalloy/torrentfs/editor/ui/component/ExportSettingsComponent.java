package com.cjmalloy.torrentfs.editor.ui.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.event.OpenFolderEvent;
import com.cjmalloy.torrentfs.editor.event.OpenFolderEvent.OpenFolderCallback;
import com.cjmalloy.torrentfs.editor.model.ExportSettings;
import com.cjmalloy.torrentfs.editor.ui.SettingsComponent;


public class ExportSettingsComponent implements SettingsComponent<ExportSettings>
{
    private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

    private static final Dimension INPUT_SIZE = new Dimension(300, 30);

    private JPanel layout;
    private JPanel torrentSaveDir;
    private JTextField torrentSaveDirInput;
    private JButton torrentSaveDirBrowse;
    private JPanel trackers;
    private JTextArea trackersInput;

    private ExportSettings value;

    public ExportSettingsComponent()
    {
        load(new ExportSettings());
    }

    @Override
    public Component getLayout()
    {
        if (layout == null)
        {
            layout = new JPanel();
            layout.setLayout(new BoxLayout(layout, BoxLayout.PAGE_AXIS));
            layout.add(getTorrentSaveDir());
            layout.add(getTrackers());
        }
        return layout;
    }

    @Override
    public ExportSettings getValue()
    {
        saveToModel();
        return value;
    }

    @Override
    public void load(ExportSettings settings)
    {
        value = settings;
        loadModel();
    }

    private JPanel getTorrentSaveDir()
    {
        if (torrentSaveDir == null)
        {
            torrentSaveDir = new JPanel();
            torrentSaveDir.setLayout(new BoxLayout(torrentSaveDir, BoxLayout.LINE_AXIS));
            torrentSaveDir.add(new Label(R.getString("torrentSaveDirLabel")));
            torrentSaveDir.add(getTorrentSaveDirInput());
            torrentSaveDir.add(getTorrentSaveDirBrowse());
        }
        return torrentSaveDir;
    }

    private JButton getTorrentSaveDirBrowse()
    {
        if (torrentSaveDirBrowse == null)
        {
            torrentSaveDirBrowse = new JButton("...");
            torrentSaveDirBrowse.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    Controller.EVENT_BUS.post(new OpenFolderEvent(new OpenFolderCallback()
                    {
                        @Override
                        public void withFolder(Path folder)
                        {
                            getTorrentSaveDirInput().setText(folder.toString());
                        }
                    }));
                }
            });
        }
        return torrentSaveDirBrowse;
    }

    private JTextField getTorrentSaveDirInput()
    {
        if (torrentSaveDirInput == null)
        {
            torrentSaveDirInput = new JTextField();
            torrentSaveDirInput.setPreferredSize(INPUT_SIZE);
        }
        return torrentSaveDirInput;
    }

    private JPanel getTrackers()
    {
        if (trackers == null)
        {
            trackers = new JPanel();
            trackers.setLayout(new BoxLayout(trackers, BoxLayout.PAGE_AXIS));
            trackers.add(new Label(R.getString("torrentTrackersLabel")));
            trackers.add(getTrackersInput());
        }
        return trackers;
    }

    private JTextArea getTrackersInput()
    {
        if (trackersInput == null)
        {
            trackersInput = new JTextArea();

        }
        return trackersInput;
    }

    private void loadModel()
    {
        try
        {
            getTorrentSaveDirInput().setText(value.torrentSaveDir.toString());
        }
        catch (Exception e)
        {
            getTorrentSaveDirInput().setText("");
        }
        String trackersText = "";
        if (value.announce != null)
        {
            for (String uri : value.announce)
            {
                trackersText += uri + "\n";
            }
        }
        getTrackersInput().setText(trackersText);
    }

    private void saveToModel()
    {
        value.torrentSaveDir = new File(getTorrentSaveDirInput().getText());
        if (getTrackersInput().getText().length() == 0)
        {
            value.announce = null;
        }
        else
        {
            value.announce = Arrays.asList(getTrackersInput().getText().split("\n"));
        }
    }
}
