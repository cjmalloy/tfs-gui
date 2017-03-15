package com.cjmalloy.torrentfs.editor.ui.fx.component;

import java.io.File;
import javafx.scene.control.TreeCell;
import javafx.scene.image.ImageView;

import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.ui.fx.model.FileTreeModel.TreeFile;
import com.cjmalloy.torrentfs.editor.ui.fx.skin.IconBundle;


@SuppressWarnings("serial")
public class TfsTreeCellRenderer extends TreeCell<TreeFile> {
  public TfsTreeCellRenderer() {
    setClosedIcon(IconBundle.get().treeClosedFolderIcon);
    setOpenIcon(IconBundle.get().treeOpenFolderIcon);
  }

  @Override
  public void updateItem(TreeFile item, boolean empty) {
    // Create a new file because TreeFile overrides .toString and that will
    // mess up code that does not expect this
    File f = item.getAbsoluteFile();
    if (getTreeView().getRoot().equals(f)) {
      setGraphic(new ImageView(IconBundle.get().treeRootIcon));
    } else if (f.toString().endsWith(".tfs")) {
      setGraphic(new ImageView(IconBundle.get().treeTfsIcon));
    } else if (MainController.get().isNested(f)) {
      if (MainController.get().isLocked(f)) {
        setGraphic(new ImageView(IconBundle.get().treeReadOnlyIcon));
      } else {
        setGraphic(new ImageView(IconBundle.get().treeNestedIcon));
      }
    } else if (f.isDirectory()) {
      if ()
    } else {
      setGraphic(new ImageView(IconBundle.get().treeFileDefaultIcon));
    }
    //TODO: other mime types
  }
}
