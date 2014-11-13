package com.cjmalloy.torrentfs.editor.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.cjmalloy.torrentfs.editor.core.Continuation;
import com.cjmalloy.torrentfs.editor.event.ConfirmEvent;
import com.cjmalloy.torrentfs.editor.event.ConfirmEvent.ConfirmCallback;
import com.cjmalloy.torrentfs.editor.event.ErrorMessage;
import com.cjmalloy.torrentfs.editor.event.FlushEvent;
import com.cjmalloy.torrentfs.editor.event.FlushEvent.FlushListener;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.cjmalloy.torrentfs.editor.model.EditorModel;


public class EditorController extends Controller<EditorModel>
{
    private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

    public List<EditorFileController> fileControllers = new ArrayList<>();

    public EditorController(EditorModel model)
    {
        this.model = model;
    }

    /**
     * Close this file immediately with no user confirmation.
     *
     * @param f
     *      the file to close
     */
    public void close(EditorFileModel f)
    {
        fileControllers.remove(getController(f));
        model.openFiles.remove(f);
        update();
    }

    /**
     * Close all files immediately with no user confirmation.
     */
    public void closeAll()
    {
        fileControllers.clear();
        model.openFiles.clear();
        update();
    }

    /**
     * Close all files. If there are unsaved changes, confirm with the user. If
     * the user cancels then then continuation will be ignored.
     *
     * @param ct
     *      the continuation to call if the user does not cancel
     */
    public void closeAll(final Continuation ct)
    {
        saveCancelContinue(new Continuation()
        {
            @Override
            public void next()
            {
                closeAll();
                ct.next();
            }
        });
    }


    public EditorFileController getController(EditorFileModel f)
    {
        for (EditorFileController c : fileControllers)
        {
            if (c.model == f) return c;
        }
        return null;
    }

    public boolean hasUnsavedChanges()
    {
        for (EditorFileController c : fileControllers)
        {
            if (c.hasUnsavedChanges()) return true;
        }
        return false;
    }

    /**
     * Close this file. If the file has unsaved changes, confirm with the user.
     * If the user cancels then then continuation will be ignored.
     *
     * @param f
     *      the file to close
     * @param ct
     *      the continuation to call if the user does not cancel
     */
    public void maybeClose(final EditorFileModel f)
    {
        saveCancelContinue(f, new Continuation()
        {
            @Override
            public void next()
            {
                close(f);
            }
        });
    }

    public void openFile(File file)
    {
        EditorFileModel editorFile = model.get(file);
        if (editorFile == null)
        {
            editorFile = new EditorFileModel(file);
            try
            {
                fileControllers.add(new EditorFileController(editorFile));
                model.openFiles.add(editorFile);
            }
            catch (IOException e)
            {
                EVENT_BUS.post(new ErrorMessage(e.getMessage()));
                return;
            }
        }
        switchTo(editorFile);
    }

    public void saveAll()
    {
        for (EditorFileController c : fileControllers)
        {
            c.save();
        }
    }

    /**
     * Prompt the user to receive feedback on whether or not they would like to
     * save all files before continuing. If there are no unsaved changes then
     * the prompt is skipped. If the user presses cancel the continuation is
     * not fired.
     *
     * @param ct
     *      the continuation to call if the user does not cancel
     */
    public void saveCancelContinue(final Continuation ct)
    {
        if (!hasUnsavedChanges())
        {
            ct.next();
        }
        else
        {
            EVENT_BUS.post(new ConfirmEvent(R.getString("saveAllFiles"), new ConfirmCallback()
            {
                @Override
                public void onCancel()
                {
                    // do nothing
                }

                @Override
                public void onNo()
                {
                    ct.next();
                }

                @Override
                public void onYes()
                {
                    final int dirtyFiles = countDirty();
                    EVENT_BUS.register(new FlushListener()
                    {
                        private int count = 0;

                        @Override
                        public void onFlush(FlushEvent event)
                        {
                            if (event.file.dirty)
                            {
                                EVENT_BUS.post(new ErrorMessage(R.getString("errorWritingFile") + event.file.path));
                                EVENT_BUS.unregister(this);
                            }
                            else
                            {
                                count++;
                                if (count >= dirtyFiles)
                                {
                                    ct.next();
                                    EVENT_BUS.unregister(this);
                                }
                            }
                        }
                    });
                    saveAll();
                }
            }));
        }
    }

    /**
     * Prompt the user to receive feedback on whether or not they would like to
     * save a file before continuing. If the file has no unsaved changes then
     * the prompt is skipped. If the user presses cancel the continuation is
     * not fired.
     *
     * @param f
     *      the file to save
     * @param ct
     *      the continuation to call if the user does not cancel
     */
    public void saveCancelContinue(EditorFileModel f, final Continuation ct)
    {
        final EditorFileController fileController = getController(f);
        if (!fileController.hasUnsavedChanges())
        {
            ct.next();
        }
        else
        {
            EVENT_BUS.post(new ConfirmEvent(R.getString("saveFile"), new ConfirmCallback()
            {
                @Override
                public void onCancel()
                {
                    // do nothing
                }

                @Override
                public void onNo()
                {
                    ct.next();
                }

                @Override
                public void onYes()
                {
                    EVENT_BUS.register(new FlushListener()
                    {
                        @Override
                        public void onFlush(FlushEvent event)
                        {
                            if (event.file != fileController.model) return;

                            if (event.file.dirty)
                            {
                                EVENT_BUS.post(new ErrorMessage(R.getString("errorWritingFile") + event.file.path));
                            }
                            else
                            {
                                ct.next();
                            }
                            EVENT_BUS.unregister(this);
                        }
                    });
                    fileController.save();
                }
            }));
        }
    }

    public void switchTo(EditorFileModel file)
    {
        model.activeFile = model.openFiles.indexOf(file);
        update();
    }

    private int countDirty()
    {
        int count = 0;
        for (EditorFileModel m : model.openFiles)
        {
            if (m.dirty) count++;
        }
        return count;
    }
}
