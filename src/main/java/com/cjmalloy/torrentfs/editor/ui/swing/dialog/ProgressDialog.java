package com.cjmalloy.torrentfs.editor.ui.swing.dialog;

import java.awt.Frame;

import javax.swing.ProgressMonitor;

import com.cjmalloy.torrentfs.editor.event.ProgressEndEvent;
import com.cjmalloy.torrentfs.editor.event.ProgressStartEvent;
import com.cjmalloy.torrentfs.editor.event.ProgressUpdateEvent;
import com.google.common.eventbus.Subscribe;


public class ProgressDialog extends Dialog
{
    private ProgressMonitor dialog;

    public ProgressDialog(Frame parent)
    {
        super(parent);
    }

    @Subscribe
    public void onStart(ProgressStartEvent event)
    {
        dialog = new ProgressMonitor(parent, "Loading...", "note", 0, 100);
        dialog.setMillisToDecideToPopup(0);
        dialog.setMillisToPopup(0);
    }

    @Subscribe
    public void onUpdate(ProgressUpdateEvent event)
    {
        dialog.setProgress((int) Math.ceil(event.p * 100));
    }

    @Subscribe
    public void onEnd(ProgressEndEvent event)
    {
        dialog.close();
        dialog = null;
    }
}
