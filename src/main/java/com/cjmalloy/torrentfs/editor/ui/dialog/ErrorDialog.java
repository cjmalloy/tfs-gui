package com.cjmalloy.torrentfs.editor.ui.dialog;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.cjmalloy.torrentfs.editor.event.ErrorMessage;
import com.google.common.eventbus.Subscribe;


public class ErrorDialog extends Dialog
{
    public ErrorDialog(JFrame parent)
    {
        super(parent);
    }

    @Subscribe
    public void setMessage(ErrorMessage event)
    {
        JOptionPane.showMessageDialog(parent, event.msg, "", JOptionPane.ERROR_MESSAGE);
        event.ct.next();
    }
}
