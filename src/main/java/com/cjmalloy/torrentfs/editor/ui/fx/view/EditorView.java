package com.cjmalloy.torrentfs.editor.ui.fx.view;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.cjmalloy.torrentfs.editor.model.EditorModel;
import com.cjmalloy.torrentfs.editor.ui.fx.component.FileEditor;
import com.google.common.eventbus.Subscribe;


public class EditorView implements View {
  private Scene scene;
  private TabPane tabs;
  private List<EditorFileModel> files = new ArrayList<>();
  private List<FileEditor> fileEditors = new ArrayList<>();
  private int currentEditor = -1;

  public EditorView() {
    Controller.EVENT_BUS.register(this);
  }

  public Scene getScene() {
    if (scene == null) {
      scene = new Scene(getWidget());
    }
    return scene;
  }

  public TabPane getWidget() {
    if (tabs == null) {
      tabs = new TabPane();
      tabs.setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);
    }
    return tabs;
  }

  @Subscribe
  public void update(EditorModel model) {
    for (EditorFileModel f : model.openFiles) {
      if (files.contains(f)) continue;

      FileEditor e = new FileEditor(f);
      files.add(f);
      fileEditors.add(e);
      Tab t = new Tab(f.getTitle(), e.getWidget());
      getWidget().getTabs().add(t);
      final EditorFileModel _f = f;
      t.setOnCloseRequest(event -> MainController.get().editor.maybeClose(_f));
    }
    for (int i = files.size() - 1; i >= 0; i--) {
      EditorFileModel f = files.get(i);
      if (model.openFiles.contains(f)) continue;

      FileEditor e = fileEditors.get(i);
      e.close();
      fileEditors.remove(i);
      files.remove(i);
      getWidget().getTabs().remove(e.getWidget());
    }

    if (currentEditor != model.activeFile) {
      currentEditor = model.activeFile;
      getWidget().getSelectionModel().select(currentEditor);
    }
  }
}
