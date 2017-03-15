package com.cjmalloy.torrentfs.editor.ui.fx.component;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.plaf.basic.BasicButtonUI;

import com.cjmalloy.torrentfs.editor.ui.fx.HasWidget;

/**
 * Component to be used as tabComponent. Contains a Label to show the text and
 * a JButton to close the tab it belongs to
 */
@SuppressWarnings("serial")
public class ButtonTabComponent implements HasWidget {
  private TabPane parent;
  private Pane widget;
  private Label label;
  private Button button;

  public ButtonTabComponent(TabPane parent, EventHandler<javafx.event.ActionEvent> l) {
    this.parent = parent;
    getButton().setOnAction(l);
  }

  public Pane getWidget() {
    if (widget == null) {
      widget = new FlowPane(Orientation.HORIZONTAL, 0, 0);
      widget.setPadding(new Insets(2, 0, 0, 0));
      widget.getChildren().addAll(getLabel(), getButton());
    }
    return widget;
  }

  private Button getButton() {
    if (button == null) {
      button = new Button();
      int size = 17;
      button.setPrefWidth(size);
      button.setPrefHeight(size);
      Tooltip.install(button, new Tooltip("close this tab"));
//      {
//        // we don't want to update UI for this button
//        @Override
//        public void updateUI() {
//        }
//
//        // paint the cross
//        @Override
//        protected void paintComponent(Graphics g) {
//          super.paintComponent(g);
//          Graphics2D g2 = (Graphics2D) g.create();
//          // shift the image for pressed buttons
//          if (getModel().isPressed()) {
//            g2.translate(1, 1);
//          }
//          g2.setStroke(new BasicStroke(2));
//          g2.setColor(Color.BLACK);
//          if (getModel().isRollover()) {
//            g2.setColor(Color.MAGENTA);
//          }
//          int delta = 6;
//          g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
//          g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
//          g2.dispose();
//        }
//      };
//      button.setPreferredSize(new Dimension(size, size));
//      button.setToolTipText();
//      // Make the button looks the same for all Laf's
//      button.setUI(new BasicButtonUI());
//      // Make it transparent
//      button.setContentAreaFilled(false);
//      // No need to be focusable
//      button.setFocusable(false);
//      button.setBorder(BorderFactory.createEtchedBorder());
//      button.setBorderPainted(false);
//      // Making nice rollover effect
//      // we use the same listener for all buttons
//      button.addMouseListener(new ButtonHoverDelegate());
//      button.setRolloverEnabled(true);
    }
    return button;
  }

  private Label getLabel() {
    if (label == null) {
      label = new Label();
      int i = parent.indexOfTabComponent(ButtonTabComponent.this.getWidget());
      if (i != -1) {
        return parent.getTitleAt(i);
      }
      label.setPadding(new Insets(0, 0, 0, 5));
    }
    return label;
  }

  private class ButtonHoverDelegate extends MouseAdapter {
    @Override
    public void mouseEntered(MouseEvent e) {
      Component component = e.getComponent();
      if (component instanceof AbstractButton) {
        AbstractButton button = (AbstractButton) component;
        button.setBorderPainted(true);
      }
    }

    @Override
    public void mouseExited(MouseEvent e) {
      Component component = e.getComponent();
      if (component instanceof AbstractButton) {
        AbstractButton button = (AbstractButton) component;
        button.setBorderPainted(false);
      }
    }
  }
}
