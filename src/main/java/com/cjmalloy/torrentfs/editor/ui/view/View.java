package com.cjmalloy.torrentfs.editor.ui.view;

import java.awt.Dimension;

import com.cjmalloy.torrentfs.editor.ui.HasWidget;


public interface View extends HasWidget
{
    void onResize(Dimension dim);
}
