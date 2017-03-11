package com.cjmalloy.torrentfs.editor.ui.swing.view;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.model.document.MainDocument;
import com.cjmalloy.torrentfs.editor.ui.swing.skin.IconBundle;
import com.google.common.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;


public class MainView implements View {
  private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

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

  public MainView() {
    Controller.EVENT_BUS.register(this);
  }

  @Override
  public Component getWidget() {
    if (widget == null) {
      widget = new JPanel();
      widget.setLayout(new BorderLayout());
      widget.add(getSplitPane(), BorderLayout.CENTER);
    }
    return widget;
  }

  @Subscribe
  public void update(MainDocument model) {

  }

  private EditorView getEditorView() {
    if (editorView == null) {
      editorView = new EditorView();
      editorView.getWidget().setMinimumSize(new Dimension(MIN_SPLIT_RIGHT, 0));
    }
    return editorView;
  }

  private JButton getExportButton() {
    if (exportButton == null) {
      exportButton = new JButton(IconBundle.get().exportIcon);
      exportButton.setToolTipText(R.getString("exportButtonTooltip"));
      exportButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          MainController.get().export();
        }
      });
    }
    return exportButton;
  }

  private FileSystemView getFilesystemView() {
    if (fileSystemView == null) {
      fileSystemView = new FileSystemView();
    }
    return fileSystemView;
  }

  private JPanel getLeftArea() {
    if (leftArea == null) {
      leftArea = new JPanel();
      leftArea.setLayout(new BorderLayout());
      leftArea.setMinimumSize(new Dimension(MIN_SPLIT_LEFT, 0));
      leftArea.add(getToolbar(), BorderLayout.NORTH);
      leftArea.add(getFilesystemView().getWidget(), BorderLayout.CENTER);
    }
    return leftArea;
  }

  private JButton getOpenButton() {
    if (openButton == null) {
      openButton = new JButton(IconBundle.get().openIcon);
      openButton.setToolTipText(R.getString("openButtonTooltip"));
      openButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          MainController.get().open();
        }
      });
    }
    return openButton;
  }

  private JSplitPane getSplitPane() {
    if (splitPane == null) {
      splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getLeftArea(), getEditorView().getWidget());
      splitPane.setDividerLocation(DEFAULT_SPLIT);
    }
    return splitPane;
  }

  private JToolBar getToolbar() {
    if (toolbar == null) {
      toolbar = new JToolBar();
      toolbar.add(getOpenButton());
      toolbar.add(getExportButton());
    }
    return toolbar;
  }
}
