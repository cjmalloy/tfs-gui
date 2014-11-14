package com.cjmalloy.torrentfs.editor.ui.dialog;

import java.awt.Frame;

import javax.swing.JFrame;

import com.cjmalloy.torrentfs.editor.controller.Controller;


public abstract class Dialog
{
    protected Frame parent;

    public Dialog(Frame parent)
    {
        this.parent = parent;
        Controller.EVENT_BUS.register(this);
    }

    public static void loadAllDialogs(JFrame parent)
    {
        new OpenFolderDialog(parent);
        new ExportDialog(parent);
        new MessageDialog(parent);
        new ErrorDialog(parent);
        new ConfirmDialog(parent);
        new PromptDialog(parent);
        new ProgressDialog(parent);
    }
}
