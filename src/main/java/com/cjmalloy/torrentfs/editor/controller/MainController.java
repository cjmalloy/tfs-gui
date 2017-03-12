package com.cjmalloy.torrentfs.editor.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.cjmalloy.torrentfs.editor.core.*;
import com.cjmalloy.torrentfs.editor.event.*;
import com.cjmalloy.torrentfs.editor.event.DoExport.ExportCallback;
import com.cjmalloy.torrentfs.editor.event.DoOpenFolder.OpenFolderCallback;
import com.cjmalloy.torrentfs.editor.model.ExportSettings;
import com.cjmalloy.torrentfs.editor.model.document.MainDocument;
import com.cjmalloy.torrentfs.editor.ui.*;
import com.cjmalloy.torrentfs.model.*;
import com.cjmalloy.torrentfs.util.TfsUtil;
import com.google.common.eventbus.Subscribe;
import com.turn.ttorrent.common.Torrent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainController extends Controller<MainDocument> {

  private static final Logger logger = LoggerFactory.getLogger(MainController.class);
  private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

  private static final String CREATOR_ID = "tfs-gui";
  private static final Encoding DEFAULT_ENCODING = Encoding.BENCODE_BASE64;

  private static MainController instance = new MainController();

  static {
    instance.model = new MainDocument();

    instance.fileSystem = new FileSystemController(instance.model.fileSystemModel);
    instance.editor = new EditorController(instance.model.editorModel);
    instance.loadMeta();
  }

  private static ExceptionHandler ioExceptionHandler = new ExceptionHandler() {
    @Override
    public void handleException(Exception e) {
      Controller.EVENT_BUS.post(new DoMessage(R.getString("errorAccessingFilesystem") + e.getLocalizedMessage()));
    }
  };

  public FileSystemController fileSystem;
  public EditorController editor;

  /**
   * Ignore file system updates while this class is writing to the
   * file system.
   */
  private boolean ignoreFsUpdates = false;

  protected MainController() {
  }

  public Nested createNested(File f) throws IOException {
    Nested parent = getParentNested(f);

    Meta parentMeta;
    File parentAbsPath;
    if (parent == null) {
      // parent is root
      parentAbsPath = model.fileSystemModel.workspace.toFile();
      if (parentAbsPath.equals(f)) return null;
      if (model.metadata == null) {
        createTfs(parentAbsPath);
      }
      parentMeta = model.metadata;
    } else {
      if (parent.absolutePath.equals(f)) return parent;
      if (parent.meta == null) createTfs(parent.absolutePath);
      parentMeta = parent.meta;
      parentAbsPath = parent.absolutePath;
    }

    Nested result = new Nested();
    result.mount = parentAbsPath.toPath().relativize(f.toPath()).toString();
    result.encoding = DEFAULT_ENCODING;
    result.absolutePath = f;
    if (parentMeta.nested == null) parentMeta.nested = new ArrayList<>();
    parentMeta.nested.add(result);
    rebaseSiblings(parentMeta, result);
    return result;
  }

  public Meta createTfs(File f) throws IOException {
    if (f.isDirectory()) f = Paths.get(f.toString(), ".tfs").toFile();
    Nested parent = createNested(f.getParentFile());
    if (parent != null && parent.meta != null) return parent.meta;

    Meta meta = new Meta();
    if (parent == null) {
      // parent is root
      model.metadata = meta;
    } else {
      parent.meta = meta;
    }
    return meta;
  }

  /**
   * Prompt the user to export the torrent files for this tfs.
   * This will update any nested torrent data if it has changed.
   */
  public void export() {
    editor.saveCancelContinue(new Continuation() {
      @Override
      public void next() {
        EVENT_BUS.post(new DoExport(new ExportCallback() {
          @Override
          public void withSettings(ExportSettings settings) {
            exportWithSettings(settings);
          }
        }));
      }
    });
  }

  @Subscribe
  public void fileModified(FileModificationEvent event) {
    if (ignoreFsUpdates) return;

    //TODO: try to only load files that have changed
    if (event.file.toString().endsWith(".tfs")) {
      loadMeta();
    }
  }

  /**
   * Get the context menu for this file.
   */
  public List<MenuItem> getMenu(final File f) {
    List<MenuItem> ret = new ArrayList<>();
    ret.add(new MenuItem(R.getString("edit"), new Continuation() {
      @Override
      public void next() {
        editor.openFile(f);
      }
    }));
    if (isLocked(f)) {
      if (isNested(f)) {
        ret.add(new MenuItem(R.getString("unlock"), new ThrowsContinuation() {
          @Override
          public void next() throws IOException {
            unlock(f);
            writeMeta();
          }
        }, ioExceptionHandler));
      }
    } else {
      if (isNested(f)) {
        ret.add(new MenuItem(R.getString("lock"), new ThrowsContinuation() {
          @Override
          public void next() throws IOException {
            lock(f);
            writeMeta();
          }
        }, ioExceptionHandler));
      }
      if (isTfs(f) || isTfsRootDir(f)) {
        ret.add(new MenuItem(R.getString("removeTfs"), new ThrowsContinuation() {
          @Override
          public void next() throws IOException {
            removeTfs(f);
            writeMeta();
          }
        }, ioExceptionHandler));
      } else {
        if (f.isDirectory()) {
          ret.add(new MenuItem(R.getString("createTfs"), new ThrowsContinuation() {
            @Override
            public void next() throws IOException {
              createTfs(f);
              writeMeta();
            }
          }, ioExceptionHandler));
        }
        if (isNested(f)) {
          ret.add(new MenuItem(R.getString("removeNested"), new ThrowsContinuation() {
            @Override
            public void next() throws IOException {
              removeNested(f);
              writeMeta();
            }
          }, ioExceptionHandler));
        } else {
          ret.add(new MenuItem(R.getString("createNested"), new ThrowsContinuation() {
            @Override
            public void next() throws IOException {
              createNested(f);
              writeMeta();
            }
          }, ioExceptionHandler));
        }
      }
    }
    return ret;
  }

  /**
   * Get the metadata for this nested torrent mount location.
   */
  public Nested getNested(File f) throws IOException {
    if (model.metadata == null) return null;
    return getNestedFromRoot(model.fileSystemModel.workspace.toFile(), model.metadata, f);
  }

  /**
   * Get the torrent that contains this file. If this is part of the root
   * tfs then return null.
   */
  public Nested getParentNested(File f) throws IOException {
    if (model.metadata == null) return null;
    return getParentNestedFromRoot(model.fileSystemModel.workspace.toFile(), model.metadata, f);
  }

  /**
   * Check if this file is under a nested torrent that is locked.
   */
  public boolean isLocked(File f) {
    try {
      Nested n;
      while ((n = getParentNested(f)) != null) {
        if (n.readOnly) return true;
        f = f.getParentFile();
      }
    } catch (IOException e) {
      logger.warn(e.getMessage());
    }
    return false;
  }

  /**
   * Check if this folder is a torrent mount point.
   */
  public boolean isMountPoint(File f) throws IOException {
    return f.equals(model.fileSystemModel.workspace.toFile())
      || isNested(f);
  }

  /**
   * Check if this folder or file is a mount point for a nested torrent.
   */
  public boolean isNested(File f) {
    try {
      return getNested(f) != null;
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * Check if this is a valid .tfs file.
   */
  public boolean isTfs(File f) {
    try {
      return f.exists()
        && f.toString().endsWith(".tfs")
        && isMountPoint(f.getParentFile());
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * Check if this is a root directory for a nested tfs.
   */
  public boolean isTfsRootDir(File f) {
    return isTfs(Paths.get(f.toString(), ".tfs").toFile());
  }

  /**
   * Read all tfs metadata from the filesystem.
   */
  public void loadMeta() {
    if (model.fileSystemModel.workspace == null) return;

    try {
      File rootTfs = Paths.get(model.fileSystemModel.workspace.toString(), ".tfs").toFile();
      if (!rootTfs.exists()) {
        model.metadata = null;
        return;
      }

      model.metadata = Meta.load(rootTfs);
      loadMetaFromRoot(model.fileSystemModel.workspace.toFile(), model.metadata);
    } catch (IOException e) {
      Controller.EVENT_BUS.post(new DoMessage(R.getString("errorAccessingFilesystem") + e.getLocalizedMessage()));
    }
  }

  /**
   * Load the metadata for this nested torrent. If this nested torrent is
   * a legacy torrent and not a tfs torrent, return null.
   */
  public Meta loadTfs(Nested n) throws IllegalStateException, IOException {
    if (!n.absolutePath.isDirectory()) return null;

    File tfs = Paths.get(n.absolutePath.toString(), ".tfs").toFile();
    if (!tfs.exists()) return null;

    return Meta.load(tfs);
  }

  public void lock(File f) throws IOException {
    getNested(f).readOnly = true;
  }

  /**
   * Prompt the user to choose a new workspace.
   */
  public void open() {
    editor.closeAll(new Continuation() {
      @Override
      public void next() {
        EVENT_BUS.post(new DoOpenFolder(new OpenFolderCallback() {
          @Override
          public void withFolder(Path folder) {
            openWorkspace(folder);
          }
        }));
      }
    });
  }

  public void removeNested(File f) throws IOException {
    getParentNested(f.getParentFile()).meta.nested.remove(getNested(f));
  }

  public void removeTfs(File f) throws IOException {
    if (f.isDirectory()) f = Paths.get(f.toString(), ".tfs").toFile();
    getParentNested(f).meta = null;
    f.delete();
  }

  public void requestExit() {
    editor.closeAll(new Continuation() {
      @Override
      public void next() {
        EVENT_BUS.post(new DoShutdownNow());
        update();
      }
    });
  }

  public void unlock(File f) throws IOException {
    Nested n;
    while ((n = getParentNested(f)) != null) {
      n.readOnly = false;
      f = f.getParentFile();
    }
  }

  @Override
  public void updateAll() {
    fileSystem.update();
    editor.update();
    update();
  }

  public void writeMeta() throws IOException {
    writeMetaToFile(model.fileSystemModel.workspace.toFile(), model.metadata);
  }

  private void exportWithSettings(final ExportSettings settings) {
    if (!settings.valid()) {
      EVENT_BUS.post(new DoErrorMessage(R.getString("errorInvalidExportSettings")));
      return;
    }

    ignoreFsUpdates = true;

    EVENT_BUS.post(new ProgressStartEvent());
    WorkerExecutor.get().execute(new Worker<Void, Double>() {
      @Override
      public Void doInBackground(WorkerContext<Void, Double> context) throws Exception {
        try {
          context.publish(0.1);
          List<Torrent> torrents = TfsUtil.generateTorrentFromTfs(
              fileSystem.model.workspace.toFile(),
              Encoding.BENCODE_BASE64,
              settings.getAnnounce(),
              CREATOR_ID,
              settings.cache,
              settings.useLinks
          );
          context.publish(0.5);
          TfsUtil.saveTorrents(settings.torrentSaveDir, torrents);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      public void done(WorkerContext<Void, Double> context) {
        EVENT_BUS.post(new ProgressEndEvent());
        EVENT_BUS.post(new DoMessage("done!"));
        ignoreFsUpdates = false;
        loadMeta();
      }

      @Override
      public void process(WorkerContext<Void, Double> context, List<Double> chunks) {
        EVENT_BUS.post(new ProgressUpdateEvent(chunks.get(0)));
      }
    });
  }

  private Nested getNestedFromRoot(File metaRoot, Meta meta, File f) throws IOException {
    if (meta.nested == null) return null;

    // Breadth-first search
    for (Nested n : meta.nested) {
      if (Paths.get(metaRoot.toString(), n.mount).toFile().getCanonicalFile().equals(f)) return n;
    }
    for (Nested n : meta.nested) {
      if (n.meta == null) continue;

      Nested result = getNestedFromRoot(Paths.get(metaRoot.toString(), n.mount).toFile(), n.meta, f);
      if (result != null) return result;
    }
    return null;
  }

  private Nested getParentNestedFromRoot(File metaRoot, Meta meta, File f) throws IOException {
    if (!f.toString().startsWith(metaRoot.toString())) return null;
    if (meta.nested == null) return null;

    for (Nested n : meta.nested) {
      if (!f.toString().startsWith(Paths.get(metaRoot.toString(), n.mount).toFile().getCanonicalFile().toString())) {
        continue;
      }

      if (n.meta == null) return n;

      Nested result = getParentNestedFromRoot(Paths.get(metaRoot.toString(), n.mount).toFile(), n.meta, f);
      if (result != null) return result;
      return n;
    }
    return null;
  }

  private void loadMetaFromRoot(File metaRoot, Meta meta) throws IllegalStateException, IOException {
    if (meta.nested == null) return;

    for (Nested n : meta.nested) {
      n.absolutePath = Paths.get(metaRoot.toString(), n.mount).toFile().getCanonicalFile();
      n.meta = loadTfs(n);
      if (n.meta == null) continue;

      loadMetaFromRoot(n.absolutePath, n.meta);
    }
  }

  /**
   * Change the current workspace.
   */
  private void openWorkspace(Path workspace) {
    editor.closeAll();
    fileSystem.setWorkspace(workspace);
    loadMeta();
  }

  private void rebaseSiblings(Meta parent, Nested child) throws IOException {
    List<Nested> toMove = new ArrayList<>();
    for (int i = parent.nested.size() - 1; i >= 0; i--) {
      Nested n = parent.nested.get(i);
      if (n == child) continue;

      if (n.absolutePath.toString().startsWith(child.absolutePath.toString())) {
        parent.nested.remove(i);
        toMove.add(n);
      }
    }
    if (toMove.size() == 0) return;

    createTfs(child.absolutePath);
    for (Nested n : toMove) {
      n.mount = child.absolutePath.toPath().relativize(n.absolutePath.toPath()).toString();
    }
    if (child.meta.nested == null) child.meta.nested = new ArrayList<>();
    child.meta.nested.addAll(toMove);
  }

  private void writeMetaToFile(File f, Meta meta) throws IOException {
    if (meta == null) return;

    TfsUtil.writeTfs(Paths.get(f.toString(), ".tfs").toFile(), meta);
    if (meta.nested == null) return;

    for (Nested n : meta.nested) {
      if (n.meta == null) continue;
      writeMetaToFile(n.absolutePath, n.meta);
    }
  }

  public static MainController get() {
    return instance;
  }
}
