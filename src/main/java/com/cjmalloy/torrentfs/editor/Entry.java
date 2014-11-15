package com.cjmalloy.torrentfs.editor;

import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.ui.TopLevel;
import com.cjmalloy.torrentfs.editor.ui.swing.SwingUi;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Entry
{

    public static void main(String[] args)
    {
        Injector injector = Guice.createInjector(new SwingUi());
        TopLevel topLevel = injector.getInstance(TopLevel.class);
        MainController.get().updateAll();
        topLevel.exitOnFinish();
    }

}
