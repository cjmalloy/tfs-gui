package com.cjmalloy.torrentfs.editor.ui.toplevel;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.event.ShutdownNowEvent;
import com.cjmalloy.torrentfs.editor.ui.dialog.ErrorDialog;
import com.cjmalloy.torrentfs.editor.ui.dialog.ExportDialog;
import com.cjmalloy.torrentfs.editor.ui.dialog.MessageDialog;
import com.cjmalloy.torrentfs.editor.ui.dialog.OpenFolderDialog;
import com.cjmalloy.torrentfs.editor.ui.dialog.ProgressDialog;
import com.cjmalloy.torrentfs.editor.ui.view.View;
import com.google.common.eventbus.Subscribe;


public class Window implements TopLevel
{
    private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");
    private static final Dimension INITIAL_SIZE = new Dimension(800, 600);

    private JFrame frame = new JFrame();
    private View view = null;

    private Thread t;
    private Object lock = new Object();
    private boolean exitNow = false;

    public Window()
    {
        frame.setTitle(R.getString("windowTitle"));
        frame.setSize(INITIAL_SIZE);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        t = new Thread()
        {
            public void run()
            {
                synchronized (lock)
                {
                    while (!exitNow) try
                    {
                        lock.wait();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                MainController.get().requestExit();
            }
        });
        frame.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                onResize();
            }
        });

        Controller.EVENT_BUS.register(this);

        new OpenFolderDialog(frame);
        new ExportDialog(frame);
        new MessageDialog(frame);
        new ErrorDialog(frame);
        new ProgressDialog(frame);
    }

    @Override
    public void exitOnFinish()
    {
        try
        {
            t.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void onResize()
    {
        if (view == null) return;

        view.onResize(frame.getSize());
    }

    @Subscribe
    public void onShutdownNow(ShutdownNowEvent event)
    {
        synchronized (lock)
        {
            exitNow = true;
            lock.notify();
        }
    }

    @Override
    public void setView(View v)
    {
        if (this.view != null)
        {
            frame.remove(view.getLayout());
        }
        this.view = v;
        if (this.view != null)
        {
            frame.add(view.getLayout());
            view.onResize(frame.getSize());
            frame.revalidate();
        }
    }
}
