package com.cjmalloy.torrentfs.editor.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.cjmalloy.torrentfs.editor.event.FlushEvent;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;


public class EditorFileController extends Controller<EditorFileModel>
{

    public EditorFileController(EditorFileModel model)
    {
        this.model = model;
    }

    public void refresh() throws IOException
    {
        load();
        model.refresh = true;
        update();
    }

    /**
     * Write this file to the filesystem. If there was an error,
     * leave model.dirty = true.
     */
    public void flush(String text)
    {
        model.flush = false;
        if (!model.dirty) return;

        model.contents = text.getBytes(Charset.forName("UTF-8"));

        FileOutputStream o = null;
        try
        {
            o = new FileOutputStream(model.path);
            o.write(model.contents);
            o.close();
            o = null;
            model.dirty = false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (o != null)
            {
                IOUtils.closeQuietly(o);
            }
            EVENT_BUS.post(new FlushEvent(model));
        }
    }

    public boolean hasUnsavedChanges()
    {
        return model.dirty;
    }

    public void load() throws IOException
    {
        BufferedInputStream inf = null;
        try
        {
            inf = new BufferedInputStream(new FileInputStream(model.path));
            model.contents = IOUtils.toByteArray(inf);
            inf.close();
            inf = null;
        }
        finally
        {
            if (inf != null)
            {
                IOUtils.closeQuietly(inf);
            }
        }
        update();
    }

    public void save()
    {
        if (!model.dirty) return;
        model.flush = true;
        update();
    }

    public void setDirty()
    {
        model.dirty = true;
    }
}
