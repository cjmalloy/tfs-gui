package com.cjmalloy.torrentfs.editor.ui.fx.component;

import java.io.IOException;
import java.util.*;
import javafx.geometry.Side;
import javafx.scene.control.TabPane;

import com.cjmalloy.torrentfs.editor.controller.*;
import com.cjmalloy.torrentfs.editor.event.DoErrorMessage;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel.Facet;
import com.cjmalloy.torrentfs.editor.ui.fx.HasWidget;
import com.cjmalloy.torrentfs.editor.ui.swing.component.FacetEditor;
import com.google.common.eventbus.Subscribe;


public class FileEditor implements HasWidget {
  private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

  private TabPane tabs;

  private EditorFileController controller;

  private List<Facet> facets = new ArrayList<>();
  private List<FacetEditor> facetEditors = new ArrayList<>();
  private int currentFacet;

  public FileEditor(EditorFileModel model) {
    this.controller = MainController.get().editor.getController(model);
    update(model);
    Controller.EVENT_BUS.register(this);
  }

  public void close() {
    Controller.EVENT_BUS.unregister(this);
    for (FacetEditor e : facetEditors) {
      e.close();
    }
    tabs.removeAll();
    facets.clear();
    facetEditors.clear();
  }

  @Override
  public TabPane getWidget() {
    if (tabs == null) {
      tabs = new TabPane();
      tabs.setSide(Side.BOTTOM);
    }
    return tabs;
  }

  @Subscribe
  public void update(EditorFileModel model) {
    if (controller.model != model) return;

    for (Facet facet : model.supportedFacets) {
      if (facets.contains(facet)) continue;

      FacetEditor c;
      try {
        c = FileEditorFactory.create(facet, controller);
        facets.add(facet);
        facetEditors.add(c);
        getWidget().addTab(getTitle(facet), c.getWidget());
      } catch (IOException e) {
        e.printStackTrace();
        Controller.EVENT_BUS.post(new DoErrorMessage(e.getMessage()));
      }
    }
    for (int i = facets.size() - 1; i >= 0; i--) {
      Facet facet = facets.get(i);
      if (model.supportedFacets.contains(facet)) continue;

      FacetEditor c = facetEditors.get(i);
      c.close();
      facetEditors.remove(i);
      facets.remove(i);
      getWidget().remove(c.getWidget());
    }

    if (currentFacet != model.editMode) {
      currentFacet = model.editMode;
      tabs.setSelectedIndex(currentFacet);
    }
  }

  private static String getTitle(Facet facet) {
    switch (facet) {
      case PROPERTIES:
        return R.getString("propertiesFacetTabTitle");
      case RAW:
        return R.getString("rawFacetTabTitle");
    }
    return null;
  }
}
