package com.cjmalloy.torrentfs.editor.ui;

import java.util.List;


public interface Worker<T> {
  T doInBackground(WorkerContext context) throws Exception;

  void updateProgress(WorkerContext context, double progress);

  void done(WorkerContext context);

  interface WorkerContext {

    void updateProgress(double progress);

    void cancel(boolean mayInterruptIfRunning);

    boolean isCancelled();
  }
}
