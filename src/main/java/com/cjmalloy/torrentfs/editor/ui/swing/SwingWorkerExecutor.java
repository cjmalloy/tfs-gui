package com.cjmalloy.torrentfs.editor.ui.swing;

import java.util.List;
import javax.swing.SwingWorker;

import com.cjmalloy.torrentfs.editor.ui.Worker;
import com.cjmalloy.torrentfs.editor.ui.Worker.WorkerContext;
import com.cjmalloy.torrentfs.editor.ui.WorkerExecutor;
import com.google.inject.Singleton;

@Singleton
public class SwingWorkerExecutor extends WorkerExecutor {
  public SwingWorkerExecutor() {
    INSTANCE = this;
  }

  @Override
  public <T> WorkerContext execute(Worker<T> worker) {
    SwingWorkerImpl<T> w = new SwingWorkerImpl<>(worker);
    w.execute();
    return w.getContext();
  }

  private static class SwingWorkerImpl<T> extends SwingWorker<T, Double> {

    private WorkerContext context = new WorkerContext() {

      @Override
      public final void updateProgress(double p) {
        SwingWorkerImpl.this.publish(p);
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

    private Worker<T> worker;

    SwingWorkerImpl(Worker<T> worker) {
      this.worker = worker;
    }

    WorkerContext getContext() {
      return context;
    }

    @Override
    protected T doInBackground() throws Exception {
      return worker.doInBackground(context);
    }

    @Override
    protected void process(List<Double> chunks) {
      worker.updateProgress(context, chunks.get(chunks.size() - 1));
    }

    @Override
    protected void done() {
      worker.done(context);
    }
  }
}
