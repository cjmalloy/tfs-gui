package com.cjmalloy.torrentfs.editor.ui.fx.component;

import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import com.cjmalloy.torrentfs.editor.ui.fx.HasWidget;


public abstract class Buttons implements HasWidget {

  private static final Insets BUTTON_INSETS = new Insets(2, 8, 2, 8);

  private Pane widget;

  @Override
  public Pane getWidget() {
    if (widget == null) {
      widget = new FlowPane();
//      widget.setBorder(UIManager.getBorder("OptionPane.buttonAreaBorder"));
      widget.getChildren().addAll(
          Arrays.stream(getButtons())
          .map(b -> createButton(b))
          .collect(Collectors.toList()));

    }
    return widget;
  }

  protected abstract ButtonModel[] getButtons();

  private Button createButton(ButtonModel b) {
    Button ret = new Button(b.title);
    ret.setDefaultButton(b.defaultButton);
    ret.setPadding(BUTTON_INSETS);
    ret.setOnAction(b.action);
    return ret;
  }

  protected static class ButtonModel {
    public String title;
    public EventHandler<ActionEvent> action;
    public boolean defaultButton = false;

    public ButtonModel() {
    }

    public ButtonModel(String title, EventHandler<ActionEvent> action) {
      this.title = title;
      this.action = action;
    }

    public ButtonModel(String title, boolean defaultButton, EventHandler<ActionEvent> action) {
      this.title = title;
      this.defaultButton = defaultButton;
      this.action = action;
    }
  }

}
