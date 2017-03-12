package com.cjmalloy.torrentfs.editor.ui.swing.skin;

import javax.swing.ImageIcon;


public class IconBundle {
  private static IconBundle instance = null;

  public final ImageIcon exportIcon = new ImageIcon(IconBundle.class.getResource("icons/document-save.png"));
  public final ImageIcon openIcon = new ImageIcon(IconBundle.class.getResource("icons/document-open.png"));

  public final ImageIcon treeClosedFolderIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/folder.png"));
  public final ImageIcon treeOpenFolderIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/folder-open.png"));
  public final ImageIcon treeRootIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/user-home.png"));
  public final ImageIcon treeNestedIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/emblem-symbolic-link.png"));
  public final ImageIcon treeTfsIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/package-x-generic.png"));
  public final ImageIcon treeReadOnlyIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/emblem-readonly.png"));
  public final ImageIcon treeFileDefaultIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/text-x-generic.png"));
  public final ImageIcon treeFileImageIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/image-x-generic.png"));
  public final ImageIcon treeFileAudioIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/audio-x-generic.png"));
  public final ImageIcon treeFileVideoIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/video-x-generic.png"));
  public final ImageIcon treeFileExecIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/application-x-executable.png"));
  public final ImageIcon treeFileHtmlIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/text-html.png"));
  public final ImageIcon treeFileScriptIcon = new ImageIcon(IconBundle.class.getResource("icons/tree/text-x-script.png"));

  private IconBundle() {
  }

  public static IconBundle get() {
    if (instance == null) {
      instance = new IconBundle();
    }
    return instance;
  }
}
