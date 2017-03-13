package com.cjmalloy.torrentfs.editor.ui.fx.view;

import javafx.scene.Parent;
import javafx.scene.Scene;

import com.cjmalloy.torrentfs.editor.ui.fx.HasWidget;

public interface View extends HasWidget {
  Scene getScene();
  Parent getWidget();
}
