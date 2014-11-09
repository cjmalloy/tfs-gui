package com.cjmalloy.torrentfs.editor.ui.view;

import java.awt.Component;
import java.awt.Dimension;


public interface View
{
    Component getLayout();
    void onResize(Dimension dim);
}
