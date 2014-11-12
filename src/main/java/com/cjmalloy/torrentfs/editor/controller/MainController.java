package com.cjmalloy.torrentfs.editor.controller;

import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.SwingWorker;

import com.cjmalloy.torrentfs.editor.event.ErrorMessage;
import com.cjmalloy.torrentfs.editor.event.ExportEvent;
import com.cjmalloy.torrentfs.editor.event.ExportEvent.ExportCallback;
import com.cjmalloy.torrentfs.editor.event.Message;
import com.cjmalloy.torrentfs.editor.event.OpenFolderEvent;
import com.cjmalloy.torrentfs.editor.event.OpenFolderEvent.OpenFolderCallback;
import com.cjmalloy.torrentfs.editor.event.ProgressEnd;
import com.cjmalloy.torrentfs.editor.event.ProgressStart;
import com.cjmalloy.torrentfs.editor.event.ProgressUpdate;
import com.cjmalloy.torrentfs.editor.event.SaveCancelContinueEvent;
import com.cjmalloy.torrentfs.editor.event.SaveCancelContinueEvent.SaveCancelContinueCallback;
import com.cjmalloy.torrentfs.editor.event.ShutdownNowEvent;
import com.cjmalloy.torrentfs.editor.model.ExportSettings;
import com.cjmalloy.torrentfs.editor.model.document.MainDocument;
import com.cjmalloy.torrentfs.util.TfsUtil;
import com.cjmalloy.torrentfs.util.TfsUtil.Encoding;
import com.turn.ttorrent.common.Torrent;


public class MainController extends Controller<MainDocument>
{
    private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

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
            EVENT_BUS.post(new ShutdownNowEvent());
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

    private void export(final ExportSettings settings)
    {
        if (!settings.valid())
        {
            EVENT_BUS.post(new ErrorMessage(R.getString("errorInvalidExportSettings")));
            return;
        }

        EVENT_BUS.post(new ProgressStart());
        new SwingWorker<Void, Double>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                publish(0.1);
                List<Torrent> torrents = TfsUtil.generateTorrentFromTfs(
                    fileSystem.model.workspace.toFile(),
                    Encoding.BENCODE_BASE64,
                    settings.getAnnounce(),
                    "tfs-gui"
                );
                publish(0.5);
                TfsUtil.saveTorrents(settings.torrentSaveDir, torrents);
                return null;
            }

            @Override
            protected void process(List<Double> chunks)
            {
                EVENT_BUS.post(new ProgressUpdate(chunks.get(0)));
            }

            @Override
            protected void done()
            {
                EVENT_BUS.post(new ProgressEnd());
                EVENT_BUS.post(new Message("done!"));
            }
        }.execute();
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
