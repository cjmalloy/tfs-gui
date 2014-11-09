package com.cjmalloy.torrentfs.editor.controller;

import java.nio.file.Path;

import com.cjmalloy.torrentfs.editor.event.ExportEvent;
import com.cjmalloy.torrentfs.editor.event.ExportEvent.ExportCallback;
import com.cjmalloy.torrentfs.editor.event.OpenFolderEvent;
import com.cjmalloy.torrentfs.editor.event.OpenFolderEvent.OpenFolderCallback;
import com.cjmalloy.torrentfs.editor.event.SaveCancelContinueEvent;
import com.cjmalloy.torrentfs.editor.event.SaveCancelContinueEvent.SaveCancelContinueCallback;
import com.cjmalloy.torrentfs.editor.model.ExportSettings;
import com.cjmalloy.torrentfs.editor.model.document.MainDocument;


public class MainController extends Controller<MainDocument>
{
    private static MainController instance;

    public FileSystemController fileSystem;
    public EditorController editor;

    private MainController()
    {
        model = new MainDocument();

        fileSystem = new FileSystemController(model.fileSystemModel);
        editor = new EditorController(model.editorModel);
    }

    public void export()
    {
        if (!editor.hasUnsavedChanges())
        {
            EVENT_BUS.post(new ExportEvent(new ExportCallback()
            {
                @Override
                public void withSettings(ExportSettings settings)
                {
                    export(settings);
                }
            }));
        }
        else
        {
            EVENT_BUS.post(new SaveCancelContinueEvent(new SaveCancelContinueAdapter()
            {
                @Override
                public void onContinue()
                {
                    export();
                }
            }));
        }
    }

    public void open()
    {
        if (!editor.hasUnsavedChanges())
        {
            EVENT_BUS.post(new OpenFolderEvent(new OpenFolderCallback()
            {
                @Override
                public void withFolder(Path folder)
                {
                    open(folder);
                }
            }));
        }
        else
        {
            EVENT_BUS.post(new SaveCancelContinueEvent(new SaveCancelContinueAdapter()
            {
                @Override
                public void onContinue()
                {
                    open();
                }
            }));
        }
    }

    public void requestExit()
    {
        if (!editor.hasUnsavedChanges())
        {
            model.exitNow = true;
            update();
        }
        else
        {
            EVENT_BUS.post(new SaveCancelContinueEvent(new SaveCancelContinueAdapter()
            {
                @Override
                public void onContinue()
                {
                    requestExit();
                }
            }));
        }
    }

    @Override
    public void updateAll()
    {
        fileSystem.update();
        editor.update();
        update();
    }

    private void export(ExportSettings settings)
    {
        // TODO Auto-generated method stub
    }

    private void open(Path workspace)
    {
        editor.closeAll();
        fileSystem.setWorkspace(workspace);
    }

    public static MainController get()
    {
        if (instance == null)
        {
            instance = new MainController();
        }
        return instance;
    }

    private abstract class SaveCancelContinueAdapter implements SaveCancelContinueCallback
    {
        @Override
        public void onCancel()
        {
            //do nothing
        }

        @Override
        public void onSave()
        {
            editor.saveAll();
            onContinue();
        }
    }
}
