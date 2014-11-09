package com.cjmalloy.torrentfs.editor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.cjmalloy.torrentfs.editor.ui.view.MainView;

public class Entry
{

    public static void main(String[] args)
    {
        MainView view = new MainView();

        final JFrame frame = new JFrame();
        frame.setSize(800, 600);
        frame.add(view.getLayout());
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setVisible(true);

        final Object lock = new Object();
        Thread t = new Thread()
        {
            public void run()
            {
                synchronized (lock)
                {
                    while (frame.isVisible()) try
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
            public void windowClosing(WindowEvent arg0)
            {
                synchronized (lock)
                {
                    frame.setVisible(false);
                    lock.notify();
                }
            }
        });

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

}
