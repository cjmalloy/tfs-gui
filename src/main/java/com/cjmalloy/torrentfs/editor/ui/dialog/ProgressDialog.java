package com.cjmalloy.torrentfs.editor.ui.dialog;

import java.awt.Frame;

import javax.swing.ProgressMonitor;

import com.cjmalloy.torrentfs.editor.event.ProgressEnd;
import com.cjmalloy.torrentfs.editor.event.ProgressStart;
import com.cjmalloy.torrentfs.editor.event.ProgressUpdate;
import com.google.common.eventbus.Subscribe;


public class ProgressDialog extends Dialog
{
    private ProgressMonitor dialog;

    public ProgressDialog(Frame parent)
    {
        super(parent);
    }

    @Subscribe
    public void onStart(ProgressStart event)
    {
        dialog = new ProgressMonitor(parent, "Loading...", "note", 0, 100);
        dialog.setMillisToDecideToPopup(0);
        dialog.setMillisToPopup(0);
    }

    @Subscribe
    public void onUpdate(ProgressUpdate event)
    {
        dialog.setProgress((int) Math.ceil(event.p * 100));
    }

    @Subscribe
    public void onEnd(ProgressEnd event)
    {
        dialog.close();
        dialog = null;
    }
}
