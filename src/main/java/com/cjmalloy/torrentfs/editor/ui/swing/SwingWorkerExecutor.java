package com.cjmalloy.torrentfs.editor.ui.swing;

import com.cjmalloy.torrentfs.editor.ui.Worker;
import com.cjmalloy.torrentfs.editor.ui.Worker.WorkerContext;
import com.cjmalloy.torrentfs.editor.ui.WorkerExecutor;
import com.google.inject.Singleton;

import javax.swing.*;
import java.util.List;

@Singleton
public class SwingWorkerExecutor extends WorkerExecutor {
  public SwingWorkerExecutor() {
    INSTANCE = this;
  }

  @Override
  public <T, V> WorkerContext<T, V> execute(Worker<T, V> worker) {
    SwingWorkerImpl<T, V> w = new SwingWorkerImpl<T, V>(worker);
    w.execute();
    return w.getContext();
  }

  private static class SwingWorkerImpl<T, V> extends SwingWorker<T, V> {
    private WorkerContext<T, V> context = new WorkerContext<T, V>() {
      @Override
      @SafeVarargs
      public final void publish(V... chunks) {
        SwingWorkerImpl.this.publish(chunks);
      }

      @Override
      public void cancel(boolean mayInterruptIfRunning) {
        SwingWorkerImpl.this.cancel(mayInterruptIfRunning);
      }

      @Override
      public boolean isCancelled() {
        return SwingWorkerImpl.this.isCancelled();
      }
    };

    private Worker<T, V> worker;

    public SwingWorkerImpl(Worker<T, V> worker) {
      this.worker = worker;
    }

    public WorkerContext<T, V> getContext() {
      return context;
    }

    @Override
    protected T doInBackground() throws Exception {
      return worker.doInBackground(context);
    }

    @Override
    protected void process(List<V> chunks) {
      worker.process(context, chunks);
    }

    @Override
    protected void done() {
      worker.done(context);
    }
  }
}
