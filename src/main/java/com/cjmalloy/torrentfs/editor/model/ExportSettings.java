package com.cjmalloy.torrentfs.editor.model;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ExportSettings
{
    private static final List<String> DEFAULT_ANNOUNCE = Arrays.asList(
        "udp://tracker.openbittorrent.com:80",
        "udp://tracker.publicbt.com:80",
        "udp://tracker.istole.it:6969",
        "udp://open.demonii.com:1337"
    );

    public File torrentSaveDir;
    public List<String> announce = DEFAULT_ANNOUNCE;

    public List<List<URI>> getAnnounce() throws URISyntaxException
    {
        if (announce == null) return new ArrayList<>();
        List<URI> announceURIs = new ArrayList<>();
        for (String s : announce)
        {
            s = s.trim();
            if (s.length() == 0) continue;
            announceURIs.add(new URI(s));
        }
        return Arrays.asList(announceURIs);
    }

    public boolean valid()
    {
        if ( torrentSaveDir == null ||
            !torrentSaveDir.exists() ||
            !torrentSaveDir.isDirectory()) return false;

        try
        {
            getAnnounce();
        }
        catch (URISyntaxException e)
        {
            return false;
        }
        return true;
    }
}
