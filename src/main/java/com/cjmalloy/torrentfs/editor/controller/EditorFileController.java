package com.cjmalloy.torrentfs.editor.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import com.cjmalloy.torrentfs.editor.event.DoFlushFile;
import com.cjmalloy.torrentfs.editor.event.FlushFileEvent;
import com.cjmalloy.torrentfs.editor.event.RefreshFileEvent;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;


public class EditorFileController extends Controller<EditorFileModel>
{

    public EditorFileController(EditorFileModel model)
    {
        this.model = model;
    }

    /**
     * Write this file to the filesystem. If there was an error,
     * leave model.editorModified = true.
     */
    public void flush(String text)
    {
        if (!model.editorModified) return;

        model.contents = text.getBytes(Charset.forName("UTF-8"));

        FileOutputStream o = null;
        try
        {
            o = new FileOutputStream(model.path);
            o.write(model.contents);
            o.close();
            o = null;
            model.editorModified = false;
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
            EVENT_BUS.post(new FlushFileEvent(model));
        }
    }

    public boolean hasUnsavedChanges()
    {
        return model.editorModified;
    }

    public void ignoreModified()
    {
        model.fileSystemModified = false;
    }

    public void load() throws IOException
    {
        FileInputStream inf = null;
        try
        {
            inf = new FileInputStream(model.path);
            byte[] fileContents = IOUtils.toByteArray(inf);
            if (!model.fileSystemModified &&
                model.contents != null &&
                !Arrays.equals(model.contents, fileContents))
            {
                model.fileSystemModified = true;
            }
            model.contents = fileContents;
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

    public void loadAndRefresh() throws IOException
    {
        load();
        refresh();
    }

    public void refresh()
    {
        model.fileSystemModified = false;
        EVENT_BUS.post(new RefreshFileEvent(model));
    }

    public void save()
    {
        if (!model.editorModified) return;
        EVENT_BUS.post(new DoFlushFile(model));
    }

    public void setDirty()
    {
        model.editorModified = true;
    }
}
