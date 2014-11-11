package com.cjmalloy.torrentfs.editor.model;

import java.io.File;


public class ExportSettings
{
    public File torrentSaveDir;

    public boolean valid()
    {
        return torrentSaveDir != null &&
               torrentSaveDir.exists() &&
               torrentSaveDir.isDirectory();
    }
}
