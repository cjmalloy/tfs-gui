package com.cjmalloy.torrentfs.editor.ui;

import java.util.List;


public interface Worker<T, V> {
  T doInBackground(WorkerContext<T, V> context) throws Exception;

  void process(WorkerContext<T, V> context, List<V> chunks);

  void done(WorkerContext<T, V> context);

  public interface WorkerContext<T, V> {
    @SuppressWarnings("unchecked")
    void publish(V... chunks);

    void cancel(boolean mayInterruptIfRunning);

    boolean isCancelled();
  }
}
