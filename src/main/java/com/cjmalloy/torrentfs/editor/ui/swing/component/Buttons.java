package com.cjmalloy.torrentfs.editor.ui.swing.component;

import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.cjmalloy.torrentfs.editor.ui.swing.HasWidget;
import com.cjmalloy.torrentfs.editor.ui.swing.layoutmanager.ButtonAreaLayout;


public abstract class Buttons implements HasWidget
{

    private static final Insets BUTTON_INSETS = new Insets(2, 8, 2, 8);

    private JPanel widget;

    @Override
    public JPanel getWidget()
    {
        if (widget == null)
        {
            widget = new JPanel();
            widget.setLayout(new ButtonAreaLayout(true, 6, SwingConstants.RIGHT, false));
            widget.setBorder(UIManager.getBorder("OptionPane.buttonAreaBorder"));
            for (Button b : getButtons())
            {
                widget.add(createButton(b));
            }
        }
        return widget;
    }

    protected abstract Button[] getButtons();

    private JButton createButton(Button b)
    {
        JButton ret = new JButton(b.title);
        ret.setMargin(BUTTON_INSETS);
        ret.addActionListener(b.action);
        return ret;
    }

    protected static class Button
    {
        public String title;
        public ActionListener action;

        public Button() {}

        public Button(String title, ActionListener action)
        {
            this.title = title;
            this.action = action;
        }
    }

}
