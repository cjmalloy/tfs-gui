package com.cjmalloy.torrentfs.editor;

import javax.swing.UIManager;

import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.ui.toplevel.Window;
import com.cjmalloy.torrentfs.editor.ui.view.MainView;

public class Entry
{

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Throwable e) {}

        Window topLevel = new Window();
        MainView view = new MainView();
        topLevel.setView(view);
        MainController.get().updateAll();
        topLevel.exitOnFinish();
    }

}
