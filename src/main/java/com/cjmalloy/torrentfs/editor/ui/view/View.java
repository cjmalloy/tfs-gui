package com.cjmalloy.torrentfs.editor.ui.view;

import java.awt.Dimension;

import com.cjmalloy.torrentfs.editor.ui.HasLayout;


public interface View extends HasLayout
{
    void onResize(Dimension dim);
}
