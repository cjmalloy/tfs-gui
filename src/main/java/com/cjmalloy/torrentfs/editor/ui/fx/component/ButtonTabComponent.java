package com.cjmalloy.torrentfs.editor.ui.fx.component;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

import com.cjmalloy.torrentfs.editor.ui.swing.HasWidget;

/**
 * Component to be used as tabComponent. Contains a JLabel to show the text and
 * a JButton to close the tab it belongs to
 */
@SuppressWarnings("serial")
public class ButtonTabComponent implements HasWidget {
  private JTabbedPane parent;
  private JPanel widget;
  private JLabel label;
  private JButton button;

  public ButtonTabComponent(JTabbedPane parent, ActionListener l) {
    this.parent = parent;
    getButton().addActionListener(l);
  }

  public JPanel getWidget() {
    if (widget == null) {
      widget = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      widget.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
      widget.setOpaque(false);
      widget.add(getLabel());
      widget.add(getButton());
    }
    return widget;
  }

  private JButton getButton() {
    if (button == null) {
      button = new JButton() {
        // we don't want to update UI for this button
        @Override
        public void updateUI() {
        }

        // paint the cross
        @Override
        protected void paintComponent(Graphics g) {
          super.paintComponent(g);
          Graphics2D g2 = (Graphics2D) g.create();
          // shift the image for pressed buttons
          if (getModel().isPressed()) {
            g2.translate(1, 1);
          }
          g2.setStroke(new BasicStroke(2));
          g2.setColor(Color.BLACK);
          if (getModel().isRollover()) {
            g2.setColor(Color.MAGENTA);
          }
          int delta = 6;
          g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
          g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
          g2.dispose();
        }
      };
      int size = 17;
      button.setPreferredSize(new Dimension(size, size));
      button.setToolTipText("close this tab");
      // Make the button looks the same for all Laf's
      button.setUI(new BasicButtonUI());
      // Make it transparent
      button.setContentAreaFilled(false);
      // No need to be focusable
      button.setFocusable(false);
      button.setBorder(BorderFactory.createEtchedBorder());
      button.setBorderPainted(false);
      // Making nice rollover effect
      // we use the same listener for all buttons
      button.addMouseListener(new ButtonHoverDelegate());
      button.setRolloverEnabled(true);
    }
    return button;
  }

  private JLabel getLabel() {
    if (label == null) {
      label = new JLabel() {
        @Override
        public String getText() {
          int i = parent.indexOfTabComponent(ButtonTabComponent.this.getWidget());
          if (i != -1) {
            return parent.getTitleAt(i);
          }
          return null;
        }
      };
      label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
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
