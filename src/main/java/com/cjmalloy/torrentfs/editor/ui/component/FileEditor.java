package com.cjmalloy.torrentfs.editor.ui.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JTabbedPane;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.EditorFileController;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.event.DoErrorMessage;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel.Facet;
import com.cjmalloy.torrentfs.editor.ui.HasWidget;
import com.cjmalloy.torrentfs.editor.ui.component.FacetEditor.FileEditorFactory;
import com.google.common.eventbus.Subscribe;


public class FileEditor implements HasWidget
{
    private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

    private JTabbedPane tabs;

    private EditorFileController controller;

    private List<Facet> facets = new ArrayList<>();
    private List<FacetEditor> facetEditors = new ArrayList<>();
    private int currentFacet;

    public FileEditor(EditorFileModel model)
    {
        this.controller = MainController.get().editor.getController(model);

        for (Facet facet : model.supportedFacets)
        {
            FacetEditor c;
            try
            {
                c = FileEditorFactory.create(facet, controller);
                facetEditors.add(c);
                getWidget().addTab(getTitle(facet), c.getWidget());
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Controller.EVENT_BUS.post(new DoErrorMessage(e.getMessage()));
            }
        }
        Controller.EVENT_BUS.register(this);
    }

    public void close()
    {
        Controller.EVENT_BUS.unregister(this);
        for (FacetEditor e : facetEditors)
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

        for (Facet facet : model.supportedFacets)
        {
            if (facets.contains(facet)) continue;

            FacetEditor c;
            try
            {
                c = FileEditorFactory.create(facet, controller);
                facets.add(facet);
                facetEditors.add(c);
                getWidget().addTab(getTitle(facet), c.getWidget());
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Controller.EVENT_BUS.post(new DoErrorMessage(e.getMessage()));
            }
        }
        for (int i=facets.size()-1; i>=0; i--)
        {
            Facet facet = facets.get(i);
            if (model.supportedFacets.contains(facet)) continue;

            FacetEditor c = facetEditors.get(i);
            c.close();
            facetEditors.remove(i);
            facets.remove(i);
            getWidget().remove(c.getWidget());
        }

        if (currentFacet != model.editMode)
        {
            currentFacet = model.editMode;
            tabs.setSelectedIndex(currentFacet);
        }
    }

    private static String getTitle(Facet facet)
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
