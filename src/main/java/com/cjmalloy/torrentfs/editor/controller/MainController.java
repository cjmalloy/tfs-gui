package com.cjmalloy.torrentfs.editor.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import com.cjmalloy.torrentfs.editor.ui.MenuItem;
import com.cjmalloy.torrentfs.editor.ui.Worker;
import com.cjmalloy.torrentfs.editor.ui.WorkerExecutor;
import com.cjmalloy.torrentfs.model.Meta;
import com.cjmalloy.torrentfs.model.Nested;
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

    protected MainController()
    {
        model = new MainDocument();

        fileSystem = new FileSystemController(model.fileSystemModel);
        editor = new EditorController(model.editorModel);
        loadMeta();
    }

    public void createNested(File f)
    {
        EVENT_BUS.post(new DoMessage("create nested: " + f));
    }

    public void createTfs(File f)
    {
        EVENT_BUS.post(new DoMessage("create tfs: " + f));
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

    public List<MenuItem> getMenu(final File f)
    {
        List<MenuItem> ret = new ArrayList<>();
        ret.add(new MenuItem(R.getString("edit"), new Continuation()
        {
            @Override
            public void next()
            {
                editor.openFile(f);
            }
        }));
        if (isTfs(f))
        {
            ret.add(new MenuItem(R.getString("removeTfs"), new Continuation()
            {
                @Override
                public void next()
                {
                    removeTfs(f);
                }
            }));
        }
        else
        {
            if (f.isDirectory())
            {
                ret.add(new MenuItem(R.getString("createTfs"), new Continuation()
                {
                    @Override
                    public void next()
                    {
                        createTfs(f);
                    }
                }));
            }
            if (isNested(f))
            {
                ret.add(new MenuItem(R.getString("removeNested"), new Continuation()
                {
                    @Override
                    public void next()
                    {
                        removeNested(f);
                    }
                }));
            }
            else
            {
                ret.add(new MenuItem(R.getString("createNested"), new Continuation()
                {
                    @Override
                    public void next()
                    {
                        createNested(f);
                    }
                }));
            }
        }
        return ret;
    }

    public Nested getNested(File f) throws IOException
    {
        return getNested(model.fileSystemModel.workspace.toFile(), model.metadata, f);
    }

    public Nested getParentNested(File f) throws IOException
    {
        return getParentNested(model.fileSystemModel.workspace.toFile(), model.metadata, f);
    }

    public Meta getTfs(File metaRoot, Nested n) throws IllegalStateException, IOException
    {
        File f = Paths.get(metaRoot.toString(), n.mount).toFile();
        if (!f.isDirectory()) return null;

        File tfs = Paths.get(f.toString(), ".tfs").toFile();
        if (!tfs.exists()) return null;

        return Meta.load(tfs);
    }

    public boolean isMountPoint(File f) throws IOException
    {
        return f.equals(model.fileSystemModel.workspace.toFile()) ||
               isNested(f);
    }

    public boolean isNested(File f)
    {
        try
        {
            return getNested(f) != null;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public boolean isTfs(File f)
    {
        try
        {
            return f.toString().endsWith(".tfs") &&
                   isMountPoint(f.getParentFile());
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public void loadMeta()
    {
        try
        {
            model.metadata = Meta.load(Paths.get(model.fileSystemModel.workspace.toString(), ".tfs").toFile());
            loadMeta(model.fileSystemModel.workspace.toFile(), model.metadata);
        }
        catch (Exception e)
        {
            Controller.EVENT_BUS.post(new DoMessage(R.getString("errorAccessingFilesystem") + e.getLocalizedMessage()));
        }
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

    public void removeNested(File f)
    {
        EVENT_BUS.post(new DoMessage("remove nested: " + f));
    }

    public void removeTfs(File f)
    {
        EVENT_BUS.post(new DoMessage("remove tfs: " + f));
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
            public void done(WorkerContext<Void, Double> context)
            {
                EVENT_BUS.post(new ProgressEndEvent());
                EVENT_BUS.post(new DoMessage("done!"));
            }

            @Override
            public void process(WorkerContext<Void, Double> context, List<Double> chunks)
            {
                EVENT_BUS.post(new ProgressUpdateEvent(chunks.get(0)));
            }
        });
    }

    private Nested getNested(File metaRoot, Meta meta, File f) throws IOException
    {
        if (meta.nested == null) return null;

        // Breadth-first search
        for (Nested n : meta.nested)
        {
            if (Paths.get(metaRoot.toString(), n.mount).toFile().getCanonicalFile().equals(f)) return n;
        }
        for (Nested n : meta.nested)
        {
            if (n.meta == null) continue;

            Nested result = getNested(Paths.get(metaRoot.toString(), n.mount).toFile(), n.meta, f);
            if (result != null) return result;
        }
        return null;
    }

    private Nested getParentNested(File metaRoot, Meta meta, File f) throws IOException
    {
        if (!f.toString().startsWith(metaRoot.toString())) return null;
        if (meta.nested == null) return null;

        for (Nested n : meta.nested)
        {
            if (!f.toString().startsWith(Paths.get(metaRoot.toString(), n.mount).toFile().getCanonicalFile().toString())) continue;

            if (n.meta == null) return n;

            Nested result = getParentNested(Paths.get(metaRoot.toString(), n.mount).toFile(), n.meta, f);
            if (result != null) return result;
        }
        return null;
    }

    private void loadMeta(File metaRoot, Meta meta) throws IllegalStateException, IOException
    {
        for (Nested n : meta.nested)
        {
            n.meta = getTfs(metaRoot, n);
            if (n.meta == null) continue;

            loadMeta(Paths.get(metaRoot.toString(), n.mount).toFile(), n.meta);
        }
    }

    private void open(Path workspace)
    {
        editor.closeAll();
        fileSystem.setWorkspace(workspace);
        loadMeta();
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
