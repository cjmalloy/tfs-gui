package com.cjmalloy.torrentfs.editor.ui.swing.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.cjmalloy.torrentfs.editor.ui.MenuItem;


public class PopupMenu
{
    private JPopupMenu widget;

    public PopupMenu(List<MenuItem> items)
    {
        for (MenuItem i : items)
        {
            getWidget().add(createItem(i));
        }
    }

    public void show(Component c, int x, int y)
    {
        getWidget().show(c, x, y);
    }

    private JPopupMenu getWidget()
    {
        if (widget == null)
        {
            widget = new JPopupMenu();
        }
        return widget;
    }

    private static JMenuItem createItem(final MenuItem i)
    {
        JMenuItem m = new JMenuItem(i.title);
        m.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                try
                {
                    i.ct.next();
                }
                catch (Exception e)
                {
                    i.handler.handleException(e);
                }
            }
        });
        return m;
    }
}
