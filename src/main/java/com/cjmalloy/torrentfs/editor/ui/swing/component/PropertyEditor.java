package com.cjmalloy.torrentfs.editor.ui.swing.component;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.EditorFileController;
import com.cjmalloy.torrentfs.editor.event.DoErrorMessage;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.cjmalloy.torrentfs.editor.model.Property;
import com.cjmalloy.torrentfs.editor.ui.swing.component.SettingsComponent.SettingsComponentFactory;
import com.google.common.eventbus.Subscribe;


public class PropertyEditor implements FacetEditor {
  private JPanel widget;
  private JPanel propertyContainer;

  private EditorFileController controller;

  private List<Property> properties = new ArrayList<>();
  private List<SettingsComponent<Property>> children = new ArrayList<>();

  public PropertyEditor(EditorFileController controller) {
    this.controller = controller;
    Controller.EVENT_BUS.register(this);
    controller.updateAll();
  }

  @Override
  public void close() {
    Controller.EVENT_BUS.unregister(this);
  }

  @Override
  public JPanel getWidget() {
    if (widget == null) {
      widget = new JPanel();
      widget.setLayout(new BorderLayout());
      widget.add(new JScrollPane(getPropertyContainer()), BorderLayout.CENTER);
    }
    return widget;
  }

  @Subscribe
  public void update(EditorFileModel model) {
    if (controller.model != model) return;

    for (Property p : model.properties) {
      if (properties.contains(p)) continue;

      SettingsComponent<Property> c;
      try {
        c = SettingsComponentFactory.create(p);
        properties.add(p);
        children.add(c);
        getWidget().add(c.getWidget());
      } catch (IOException e) {
        e.printStackTrace();
        Controller.EVENT_BUS.post(new DoErrorMessage(e.getMessage()));
      }
    }
    for (int i = properties.size() - 1; i >= 0; i--) {
      Property p = properties.get(i);
      if (model.properties.contains(p)) continue;

      SettingsComponent<Property> c = children.get(i);
      children.remove(i);
      properties.remove(i);
      getWidget().remove(c.getWidget());
    }
  }

  private JPanel getPropertyContainer() {
    if (propertyContainer == null) {
      propertyContainer = new JPanel();
      propertyContainer.setLayout(new BoxLayout(propertyContainer, BoxLayout.PAGE_AXIS));
    }
    return propertyContainer;
  }

}
