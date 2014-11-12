package com.cjmalloy.torrentfs.editor.ui.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

    private static final int TOOLBAR_HEIGHT = 50;
    private static final int DEFAULT_SPLIT = 300;
    private static final int MIN_SPLIT_LEFT = 100;
    private static final int MIN_SPLIT_RIGHT = 100;

    private JPanel widget;
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
    }

    @Override
    public Component getWidget()
    {
        if (widget == null)
        {
            widget = new JPanel();
            widget.setLayout(null);
            widget.add(getSplitPane());
        }
        return widget;
    }

    @Override
    public void onResize(Dimension dim)
    {
        getWidget().setSize(dim);
        getSplitPane().setSize(dim);
        onSliderMove(dim);
    }

    private void onSliderMove(Dimension dim)
    {
        if (dim.width == 0) return;
        int l = getSplitPane().getDividerLocation();
        int r = dim.width - l - getSplitPane().getDividerSize();
        getLeftArea().setSize(l, dim.height);
        getToolbar().setSize(l, TOOLBAR_HEIGHT);
        getFilesystemView().onResize(new Dimension(l, dim.height - TOOLBAR_HEIGHT));
        getEditorView().onResize(new Dimension(r, dim.height));
    }

    @Subscribe
    public void update(MainDocument model)
    {

    }

    private EditorView getEditorView()
    {
        if (editorView == null)
        {
            editorView = new EditorView();
            editorView.getWidget().setMinimumSize(new Dimension(MIN_SPLIT_RIGHT, 0));
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
                    MainController.get().export();
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
            leftArea.setLayout(null);
            leftArea.setMinimumSize(new Dimension(MIN_SPLIT_LEFT, 0));
            leftArea.add(getToolbar());
            leftArea.add(getFilesystemView().getWidget());
            getToolbar().setLocation(0, 0);
            getFilesystemView().getWidget().setLocation(0, TOOLBAR_HEIGHT);
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
                    MainController.get().open();
                }
            });
        }
        return openButton;
    }

    private JSplitPane getSplitPane()
    {
        if (splitPane == null)
        {
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getLeftArea(), getEditorView().getWidget());
            splitPane.setDividerLocation(DEFAULT_SPLIT);
            splitPane.addPropertyChangeListener(new PropertyChangeListener()
            {
                @Override
                public void propertyChange(PropertyChangeEvent evt)
                {
                    onSliderMove(getWidget().getSize());
                }
            });
        }
        return splitPane;
    }

    private JToolBar getToolbar()
    {
        if (toolbar == null)
        {
            toolbar = new JToolBar();
            toolbar.setFloatable(false);
            toolbar.add(getOpenButton());
            toolbar.add(getExportButton());
        }
        return toolbar;
    }
}
