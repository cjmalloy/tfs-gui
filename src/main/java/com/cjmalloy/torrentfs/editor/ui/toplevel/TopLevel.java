package com.cjmalloy.torrentfs.editor.ui.toplevel;

import com.cjmalloy.torrentfs.editor.ui.view.View;


public interface TopLevel
{
    /**
     * Sets the view for this top level container. Only one view can be set at
     * a time. If a view has already been set it will be removed first. Calling
     * this method with <code>null</code> will remove the view if it has been
     * set.
     */
    void setView(View v);

    /**
     * Exit the program when this container is closed.
     *
     * This method will never return.
     */
    void exitOnFinish();
}
