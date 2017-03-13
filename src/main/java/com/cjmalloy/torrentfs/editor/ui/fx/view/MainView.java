package com.cjmalloy.torrentfs.editor.ui.fx.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.model.document.MainDocument;
import com.cjmalloy.torrentfs.editor.ui.swing.skin.IconBundle;
import com.google.common.eventbus.Subscribe;


public class MainView implements View {
  private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

  private static final int DEFAULT_SPLIT = 300;
  private static final int MIN_SPLIT_LEFT = 100;
  private static final int MIN_SPLIT_RIGHT = 100;

  private Scene scene;
  private BorderPane parent;
  private SplitPane splitPane;
  private Pane leftArea;
  private EditorView editorView;
  private ToolBar toolbar;
  private FileSystemView fileSystemView;
  private Button openButton;
  private Button exportButton;

  public MainView() {
    Controller.EVENT_BUS.register(this);
  }

  public Scene getScene() {
    if (scene == null) {
      parent = new BorderPane();
      scene = new Scene(parent);
      parent.setCenter(getSplitPane());
    }
    return scene;
  }

  @Subscribe
  public void update(MainDocument model) {

  }

  private EditorView getEditorView() {
    if (editorView == null) {
      editorView = new EditorView();
      editorView.getScene().setMinimumSize(new Dimension(MIN_SPLIT_RIGHT, 0));
    }
    return editorView;
  }

  private Button getExportButton() {
    if (exportButton == null) {
      exportButton = new Button(IconBundle.get().exportIcon);
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

  private Pane getLeftArea() {
    if (leftArea == null) {
      leftArea = new Pane();
      leftArea.setLayout(new BorderLayout());
      leftArea.setMinimumSize(new Dimension(MIN_SPLIT_LEFT, 0));
      leftArea.add(getToolbar(), BorderLayout.NORTH);
      leftArea.add(getFilesystemView().getScene(), BorderLayout.CENTER);
    }
    return leftArea;
  }

  private Button getOpenButton() {
    if (openButton == null) {
      openButton = new Button(IconBundle.get().openIcon);
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

  private SplitPane getSplitPane() {
    if (splitPane == null) {
      splitPane = new SplitPane();
      splitPane.getItems().addAll(getLeftArea(), getEditorView().getScene());
      splitPane.setDividerLocation(DEFAULT_SPLIT);
    }
    return splitPane;
  }

  private ToolBar getToolbar() {
    if (toolbar == null) {
      toolbar = new ToolBar();
      toolbar.add(getOpenButton());
      toolbar.add(getExportButton());
    }
    return toolbar;
  }
}
