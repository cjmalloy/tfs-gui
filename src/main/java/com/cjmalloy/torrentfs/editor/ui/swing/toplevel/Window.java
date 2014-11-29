package com.cjmalloy.torrentfs.editor.ui.swing.toplevel;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.event.DoShutdownNow;
import com.cjmalloy.torrentfs.editor.ui.TopLevel;
import com.cjmalloy.torrentfs.editor.ui.swing.dialog.Dialog;
import com.cjmalloy.torrentfs.editor.ui.swing.view.MainView;
import com.cjmalloy.torrentfs.editor.ui.swing.view.View;
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
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "tfs-gui");
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Throwable e) {}

        frame.setTitle(R.getString("windowTitle"));
        frame.setSize(INITIAL_SIZE);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        t = new Thread()
        {
            @Override
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

        Controller.EVENT_BUS.register(this);

        Dialog.loadAllDialogs(frame);
        setView(new MainView());
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

    @Subscribe
    public void onShutdownNow(DoShutdownNow event)
    {
        synchronized (lock)
        {
            exitNow = true;
            lock.notify();
        }
    }

    public void setView(View v)
    {
        if (this.view != null)
        {
            frame.remove(view.getWidget());
        }
        this.view = v;
        if (this.view != null)
        {
            frame.add(view.getWidget());
            frame.revalidate();
        }
    }
}
