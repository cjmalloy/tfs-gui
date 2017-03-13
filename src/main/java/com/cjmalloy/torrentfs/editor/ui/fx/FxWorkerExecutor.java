package com.cjmalloy.torrentfs.editor.ui.fx;

import com.cjmalloy.torrentfs.editor.ui.Worker;
import com.cjmalloy.torrentfs.editor.ui.Worker.WorkerContext;
import com.cjmalloy.torrentfs.editor.ui.WorkerExecutor;
import com.google.inject.Singleton;
import javafx.concurrent.Task;

@Singleton
public class FxWorkerExecutor extends WorkerExecutor {
  public FxWorkerExecutor() {
    INSTANCE = this;
  }

  @Override
  public <T> WorkerContext execute(Worker<T> worker) {
    TaskImpl<T> t = new TaskImpl<>(worker);
    new Thread(t).start();
    return t.getContext();
  }

  private static class TaskImpl<T> extends Task<T> {

    private WorkerContext context = new WorkerContext() {

      @Override
      public void updateProgress(double p) {
        TaskImpl.this.updateProgress(p, 1);
      }

      @Override
      public void cancel(boolean mayInterruptIfRunning) {
        TaskImpl.this.cancel(mayInterruptIfRunning);
      }

      @Override
      public boolean isCancelled() {
        return TaskImpl.this.isCancelled();
      }
    };

    private Worker<T> worker;

    TaskImpl(Worker<T> worker) {
      this.worker = worker;
    }

    WorkerContext getContext() {
      return context;
    }

    @Override
    protected T call() throws Exception {
      return worker.doInBackground(context);
    }
  }
}
