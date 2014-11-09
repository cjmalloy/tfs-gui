package com.cjmalloy.torrentfs.editor.ui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.model.document.MainDocument;
import com.cjmalloy.torrentfs.editor.ui.skin.IconBundle;
import com.google.common.eventbus.Subscribe;



public class MainView implements View
{
    private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

    private JPanel layout;
    private JSplitPane splitPane;
    private JPanel leftArea;
    private EditorView editorView;
    private JToolBar toolbar;
    private FileSystemView fileSystemView;
    private JButton openButton;
    private JButton exportButton;

    public MainView()
    {
        Controller.EVENT_BUS.register(this);
        MainController.get().update();
    }

    @Override
    public Component getLayout()
    {
        if (layout == null)
        {
            layout = new JPanel();
            layout.add(getSplitPane(), BorderLayout.CENTER);
        }
        return layout;
    }

    @Subscribe
    protected void update(MainDocument model)
    {

    }

    private EditorView getEditorView()
    {
        if (editorView == null)
        {
            editorView = new EditorView();
        }
        return editorView;
    }

    private JButton getExportButton()
    {
        if (exportButton == null)
        {
            exportButton = new JButton(IconBundle.get().getExportIcon());
            exportButton.setToolTipText(R.getString("exportButtonTooltip"));
            exportButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    // TODO Auto-generated method stub
                }
            });
        }
        return exportButton;
    }

    private FileSystemView getFilesystemView()
    {
        if (fileSystemView == null)
        {
            fileSystemView = new FileSystemView();
        }
        return fileSystemView;
    }

    private JPanel getLeftArea()
    {
        if (leftArea == null)
        {
            leftArea = new JPanel();
            leftArea.add(getToolbar(), BorderLayout.PAGE_START);
            leftArea.add(getFilesystemView().getLayout(), BorderLayout.CENTER);
        }
        return leftArea;
    }

    private JButton getOpenButton()
    {
        if (openButton == null)
        {
            openButton = new JButton(IconBundle.get().getOpenIcon());
            openButton.setToolTipText(R.getString("openButtonTooltip"));
            openButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    // TODO Auto-generated method stub
                }
            });
        }
        return openButton;
    }

    private JSplitPane getSplitPane()
    {
        if (splitPane == null)
        {
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getLeftArea(), getEditorView().getLayout());
        }
        return splitPane;
    }

    private JToolBar getToolbar()
    {
        if (toolbar == null)
        {
            toolbar = new JToolBar();
            toolbar.add(getOpenButton());
            toolbar.add(getExportButton());
        }
        return toolbar;
    }
}
