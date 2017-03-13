package com.cjmalloy.torrentfs.editor.ui.fx.view;

import java.util.ResourceBundle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.model.document.MainDocument;
import com.cjmalloy.torrentfs.editor.ui.fx.skin.IconBundle;
import com.google.common.eventbus.Subscribe;


public class MainView implements View {
  private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

  private static final int DEFAULT_SPLIT = 300;
  private static final int MIN_SPLIT_LEFT = 100;
  private static final int MIN_SPLIT_RIGHT = 100;

  private Scene scene;
  private BorderPane parent;
  private SplitPane splitPane;
  private BorderPane leftArea;
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
      scene = new Scene(getWidget());
    }
    return scene;
  }

  public Parent getWidget() {
    if (parent == null) {
      parent = new BorderPane();
      parent.setCenter(getSplitPane());
    }
    return parent;
  }

  @Subscribe
  public void update(MainDocument model) {

  }

  private EditorView getEditorView() {
    if (editorView == null) {
      editorView = new EditorView();
      editorView.getWidget().setMinWidth(MIN_SPLIT_RIGHT);
    }
    return editorView;
  }

  private Button getExportButton() {
    if (exportButton == null) {
      exportButton = new Button("", new ImageView(IconBundle.get().exportIcon));
      Tooltip.install(exportButton, new Tooltip(R.getString("exportButtonTooltip")));
      exportButton.setOnAction(e -> MainController.get().export());
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
      leftArea = new BorderPane();
      leftArea.setMinWidth(MIN_SPLIT_LEFT);
      leftArea.setTop(getToolbar());
      leftArea.setCenter(getFilesystemView().getWidget());
    }
    return leftArea;
  }

  private Button getOpenButton() {
    if (openButton == null) {
      openButton = new Button("", new ImageView(IconBundle.get().openIcon));
      Tooltip.install(openButton, new Tooltip(R.getString("openButtonTooltip")));
      openButton.setOnAction(e -> MainController.get().open());
    }
    return openButton;
  }

  private SplitPane getSplitPane() {
    if (splitPane == null) {
      splitPane = new SplitPane(getLeftArea(), getEditorView().getWidget());
      splitPane.setDividerPositions(DEFAULT_SPLIT);
    }
    return splitPane;
  }

  private ToolBar getToolbar() {
    if (toolbar == null) {
      toolbar = new ToolBar(
        getOpenButton(),
        getExportButton());
    }
    return toolbar;
  }
}
