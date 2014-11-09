package com.cjmalloy.torrentfs.editor.controller;

import com.cjmalloy.torrentfs.editor.model.document.MainDocument;


public class MainController extends Controller<MainDocument>
{

    private static MainController instance;

    private MainController()
    {
        model = new MainDocument();
    }

    public static MainController get()
    {
        if (instance == null)
        {
            instance = new MainController();
        }
        return instance;
    }
}
