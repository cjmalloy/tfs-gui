package com.cjmalloy.torrentfs.editor.controller;

import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

import com.cjmalloy.torrentfs.editor.core.Continuation;
import com.cjmalloy.torrentfs.editor.event.DoErrorMessage;
import com.cjmalloy.torrentfs.editor.event.DoExport;
import com.cjmalloy.torrentfs.editor.event.DoExport.ExportCallback;
import com.cjmalloy.torrentfs.editor.event.DoMessage;
import com.cjmalloy.torrentfs.editor.event.DoOpenFolder;
import com.cjmalloy.torrentfs.editor.event.DoOpenFolder.OpenFolderCallback;
import com.cjmalloy.torrentfs.editor.event.DoShutdownNow;
import com.cjmalloy.torrentfs.editor.event.ProgressEndEvent;
import com.cjmalloy.torrentfs.editor.event.ProgressStartEvent;
import com.cjmalloy.torrentfs.editor.event.ProgressUpdateEvent;
import com.cjmalloy.torrentfs.editor.model.ExportSettings;
import com.cjmalloy.torrentfs.editor.model.document.MainDocument;
import com.cjmalloy.torrentfs.editor.ui.Worker;
import com.cjmalloy.torrentfs.editor.ui.WorkerExecutor;
import com.cjmalloy.torrentfs.util.TfsUtil;
import com.cjmalloy.torrentfs.util.TfsUtil.Encoding;
import com.turn.ttorrent.common.Torrent;


public class MainController extends Controller<MainDocument>
{
    private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

    private static final String CREATOR_ID = "tfs-gui";

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
                EVENT_BUS.post(new DoExport(new ExportCallback()
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
                EVENT_BUS.post(new DoOpenFolder(new OpenFolderCallback()
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
                EVENT_BUS.post(new DoShutdownNow());
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
            EVENT_BUS.post(new DoErrorMessage(R.getString("errorInvalidExportSettings")));
            return;
        }

        EVENT_BUS.post(new ProgressStartEvent());
        WorkerExecutor.get().execute(new Worker<Void, Double>()
        {
            @Override
            public Void doInBackground(WorkerContext<Void, Double> context) throws Exception
            {
                context.publish(0.1);
                List<Torrent> torrents = TfsUtil.generateTorrentFromTfs(
                    fileSystem.model.workspace.toFile(),
                    Encoding.BENCODE_BASE64,
                    settings.getAnnounce(),
                    CREATOR_ID
                );
                context.publish(0.5);
                TfsUtil.saveTorrents(settings.torrentSaveDir, torrents);
                return null;
            }

            @Override
            public void process(WorkerContext<Void, Double> context, List<Double> chunks)
            {
                EVENT_BUS.post(new ProgressUpdateEvent(chunks.get(0)));
            }

            @Override
            public void done(WorkerContext<Void, Double> context)
            {
                EVENT_BUS.post(new ProgressEndEvent());
                EVENT_BUS.post(new DoMessage("done!"));
            }
        });
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
