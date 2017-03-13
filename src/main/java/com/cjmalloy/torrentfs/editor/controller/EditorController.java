package com.cjmalloy.torrentfs.editor.controller;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.cjmalloy.torrentfs.editor.core.Continuation;
import com.cjmalloy.torrentfs.editor.event.*;
import com.cjmalloy.torrentfs.editor.event.DoConfirm.ConfirmCallback;
import com.cjmalloy.torrentfs.editor.event.DoPrompt.PromptCallback;
import com.cjmalloy.torrentfs.editor.event.FlushFileEvent.FlushListener;
import com.cjmalloy.torrentfs.editor.model.EditorFileModel;
import com.cjmalloy.torrentfs.editor.model.EditorModel;
import com.google.common.eventbus.Subscribe;


public class EditorController extends Controller<EditorModel> {
  private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

  public List<EditorFileController> fileControllers = new ArrayList<>();

  public EditorController(EditorModel model) {
    this.model = model;
  }

  /**
   * Close this file immediately with no user confirmation.
   *
   * @param f the file to close
   */
  public void close(EditorFileModel f) {
    fileControllers.remove(getController(f));
    model.openFiles.remove(f);
    model.activeFile--;
    update();
  }

  /**
   * Close all files immediately with no user confirmation.
   */
  public void closeAll() {
    fileControllers.clear();
    model.openFiles.clear();
    model.activeFile = -1;
    update();
  }

  /**
   * Close all files. If there are unsaved changes, confirm with the user. If
   * the user cancels then then continuation will be ignored.
   *
   * @param ct the continuation to call if the user does not cancel
   */
  public void closeAll(final Continuation ct) {
    saveCancelContinue(new Continuation() {
      @Override
      public void next() {
        closeAll();
        ct.next();
      }
    });
  }

  @Subscribe
  public void fileModified(FileModificationEvent event) {
    for (EditorFileController c : fileControllers) {
      if (c.model.path.toPath().equals(event.file)) {
        try {
          c.load();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    reloadActiveModified(Continuation.NOP);
  }

  public EditorFileController getActiveFileController() {
    if (model.activeFile == -1) return null;
    return getController(model.openFiles.get(model.activeFile));
  }

  public EditorFileController getController(EditorFileModel f) {
    for (EditorFileController c : fileControllers) {
      if (c.model == f) return c;
    }
    return null;
  }

  public boolean hasUnsavedChanges() {
    for (EditorFileController c : fileControllers) {
      if (c.hasUnsavedChanges()) return true;
    }
    return false;
  }

  /**
   * Close this file. If the file has unsaved changes, confirm with the user.
   * If the user cancels then then continuation will be ignored.
   *
   * @param f  the file to close
   */
  public void maybeClose(final EditorFileModel f) {
    saveCancelContinue(f, new Continuation() {
      @Override
      public void next() {
        close(f);
      }
    });
  }

  public void openFile(File file) {
    EditorFileModel editorFile = model.get(file);
    if (editorFile == null) {
      editorFile = new EditorFileModel(file);
      model.openFiles.add(editorFile);
      fileControllers.add(new EditorFileController(editorFile));
    }
    switchTo(editorFile);
  }

  public void reloadActiveModified(final Continuation ct) {
    final EditorFileController c = getActiveFileController();
    if (c != null && c.model.fileSystemModified) {
      EVENT_BUS.post(new DoPrompt(R.getString("promptReloadModifiedFile"), new PromptCallback() {
        @Override
        public void onNo() {
          c.ignoreModified();
          ct.next();
        }

        @Override
        public void onYes() {
          c.refresh();
          ct.next();
        }
      }));
    }
  }

  public void saveAll() {
    for (EditorFileController c : fileControllers) {
      c.save();
    }
  }

  /**
   * Prompt the user to receive feedback on whether or not they would like to
   * save all files before continuing. If there are no unsaved changes then
   * the prompt is skipped. If the user presses cancel the continuation is
   * not fired.
   *
   * @param ct the continuation to call if the user does not cancel
   */
  public void saveCancelContinue(final Continuation ct) {
    if (!hasUnsavedChanges()) {
      ct.next();
    } else {
      EVENT_BUS.post(new DoConfirm(R.getString("saveAllFiles"), new ConfirmCallback() {
        @Override
        public void onCancel() {
          // do nothing
        }

        @Override
        public void onNo() {
          ct.next();
        }

        @Override
        public void onYes() {
          final int dirtyFiles = countDirty();
          EVENT_BUS.register(new FlushListener() {
            private int count = 0;

            @Override
            public void onFlush(FlushFileEvent event) {
              if (event.file.editorModified) {
                EVENT_BUS.post(new DoErrorMessage(R.getString("errorWritingFile") + event.file.path));
                EVENT_BUS.unregister(this);
              } else {
                count++;
                if (count >= dirtyFiles) {
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
   * @param f  the file to save
   * @param ct the continuation to call if the user does not cancel
   */
  public void saveCancelContinue(EditorFileModel f, final Continuation ct) {
    final EditorFileController fileController = getController(f);
    if (!fileController.hasUnsavedChanges()) {
      ct.next();
    } else {
      EVENT_BUS.post(new DoConfirm(R.getString("saveFile"), new ConfirmCallback() {
        @Override
        public void onCancel() {
          // do nothing
        }

        @Override
        public void onNo() {
          ct.next();
        }

        @Override
        public void onYes() {
          EVENT_BUS.register(new FlushListener() {
            @Override
            public void onFlush(FlushFileEvent event) {
              if (event.file != fileController.model) return;

              if (event.file.editorModified) {
                EVENT_BUS.post(new DoErrorMessage(R.getString("errorWritingFile") + event.file.path));
              } else {
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

  public void switchTo(EditorFileModel file) {
    model.activeFile = model.openFiles.indexOf(file);
    update();
    reloadActiveModified(Continuation.NOP);
  }

  private int countDirty() {
    int count = 0;
    for (EditorFileModel m : model.openFiles) {
      if (m.editorModified) count++;
    }
    return count;
  }
}
