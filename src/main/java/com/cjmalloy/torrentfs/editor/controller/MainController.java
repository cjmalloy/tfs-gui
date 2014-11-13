package com.cjmalloy.torrentfs.editor.controller;

import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.SwingWorker;

import com.cjmalloy.torrentfs.editor.core.Continuation;
import com.cjmalloy.torrentfs.editor.event.ErrorMessage;
import com.cjmalloy.torrentfs.editor.event.ExportEvent;
import com.cjmalloy.torrentfs.editor.event.ExportEvent.ExportCallback;
import com.cjmalloy.torrentfs.editor.event.Message;
import com.cjmalloy.torrentfs.editor.event.OpenFolderEvent;
import com.cjmalloy.torrentfs.editor.event.OpenFolderEvent.OpenFolderCallback;
import com.cjmalloy.torrentfs.editor.event.ProgressEnd;
import com.cjmalloy.torrentfs.editor.event.ProgressStart;
import com.cjmalloy.torrentfs.editor.event.ProgressUpdate;
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
        editor.saveCancelContinue(new Continuation()
        {
            @Override
            public void next()
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
        });
    }

    public void open()
    {
        editor.closeAll(new Continuation()
        {
            @Override
            public void next()
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
        });
    }

    public void requestExit()
    {
        editor.closeAll(new Continuation()
        {
            @Override
            public void next()
            {
                EVENT_BUS.post(new ShutdownNowEvent());
                update();
            }
        });
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
}
