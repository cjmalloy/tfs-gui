package com.cjmalloy.torrentfs.editor.ui;

import com.cjmalloy.torrentfs.editor.ui.Worker.WorkerContext;


public abstract class WorkerExecutor
{
    protected static WorkerExecutor INSTANCE;

    public abstract <T,V> WorkerContext<T,V> execute(Worker<T, V> worker);

    public static WorkerExecutor get()
    {
        return INSTANCE;
    }
}
