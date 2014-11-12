package com.cjmalloy.torrentfs.editor.ui.component;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JTabbedPane;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.EditorFileController;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel.EditFacet;
import com.cjmalloy.torrentfs.editor.ui.HasWidget;
import com.cjmalloy.torrentfs.editor.ui.component.FileEditorFacet.FileEditorFactory;
import com.google.common.eventbus.Subscribe;


public class FacetContainer implements HasWidget
{
    private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

    private JTabbedPane tabs;
    private List<FileEditorFacet> facetEditors = new ArrayList<>();

    private EditorFileController controller;
    private int currentFacet;

    public FacetContainer(EditorFileModel model)
    {
        this.controller = MainController.get().editor.getController(model);

        for (EditFacet facet : model.supportedFacets)
        {
            FileEditorFacet e = FileEditorFactory.create(facet, controller);
            facetEditors.add(e);
            getWidget().addTab(getTitle(facet), e.getWidget());
        }
        Controller.EVENT_BUS.register(this);
    }

    public void close()
    {
        Controller.EVENT_BUS.unregister(this);
        for (FileEditorFacet e : facetEditors)
        {
            e.close();
        }
        tabs.removeAll();
        facetEditors.clear();
    }

    @Override
    public JTabbedPane getWidget()
    {
        if (tabs == null)
        {
            tabs = new JTabbedPane();
            tabs.setTabPlacement(JTabbedPane.BOTTOM);
        }
        return tabs;
    }

    @Subscribe
    public void update(EditorFileModel model)
    {
        if (controller.model != model) return;

        if (currentFacet != model.editMode)
        {
            currentFacet = model.editMode;
            tabs.setSelectedIndex(currentFacet);
        }
    }

    private static String getTitle(EditFacet facet)
    {
        switch (facet)
        {
        case PROPERTIES:
            return R.getString("propertiesFacetTabTitle");
        case RAW:
            return R.getString("rawFacetTabTitle");
        }
        return null;
    }
}
