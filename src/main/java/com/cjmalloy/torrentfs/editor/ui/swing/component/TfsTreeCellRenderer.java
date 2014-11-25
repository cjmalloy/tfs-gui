package com.cjmalloy.torrentfs.editor.ui.swing.component;

import java.awt.Component;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.cjmalloy.torrentfs.editor.controller.MainController;
import com.cjmalloy.torrentfs.editor.ui.swing.model.FileTreeModel.TreeFile;
import com.cjmalloy.torrentfs.editor.ui.swing.skin.IconBundle;


@SuppressWarnings("serial")
public class TfsTreeCellRenderer extends DefaultTreeCellRenderer
{
    public TfsTreeCellRenderer()
    {
        setClosedIcon(IconBundle.get().treeClosedFolderIcon);
        setOpenIcon(IconBundle.get().treeOpenFolderIcon);
        setLeafIcon(IconBundle.get().treeFileDefaultIcon);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if (!(value instanceof TreeFile)) return this;

        // Create a new file because TreeFile overrides .toString and that will
        // mess up code that does not expect this
        File f = ((TreeFile) value).getAbsoluteFile();
        if (tree.getModel().getRoot().equals(f))
        {
            setIcon(IconBundle.get().treeRootIcon);
        }
        else if (f.toString().endsWith(".tfs"))
        {
            setIcon(IconBundle.get().treeTfsIcon);
        }
        else if (MainController.get().isNested(f))
        {
            if (MainController.get().isLocked(f))
            {
                setIcon(IconBundle.get().treeReadOnlyIcon);
            }
            else
            {
                setIcon(IconBundle.get().treeNestedIcon);
            }
        }
        //TODO: other mime types
        return this;
    }
}
