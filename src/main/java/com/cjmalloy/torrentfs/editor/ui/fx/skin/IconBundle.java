package com.cjmalloy.torrentfs.editor.ui.fx.skin;

import javafx.scene.image.Image;

public class IconBundle {
  private static IconBundle instance = null;

  public final Image exportIcon = new Image(IconBundle.class.getResourceAsStream("icons/document-save.png"));
  public final Image openIcon = new Image(IconBundle.class.getResourceAsStream("icons/document-open.png"));

  public final Image treeClosedFolderIcon = new Image(IconBundle.class.getResourceAsStream("icons/tree/folder.png"));
  public final Image treeOpenFolderIcon = new Image(IconBundle.class.getResourceAsStream("icons/tree/folder-open.png"));
  public final Image treeRootIcon = new Image(IconBundle.class.getResourceAsStream("icons/tree/user-home.png"));
  public final Image treeNestedIcon = new Image(IconBundle.class.getResourceAsStream("icons/tree/emblem-symbolic-link.png"));
  public final Image treeTfsIcon = new Image(IconBundle.class.getResourceAsStream("icons/tree/package-x-generic.png"));
  public final Image treeReadOnlyIcon = new Image(IconBundle.class.getResourceAsStream("icons/tree/emblem-readonly.png"));
  public final Image treeFileDefaultIcon = new Image(IconBundle.class.getResourceAsStream("icons/tree/text-x-generic.png"));
  public final Image treeFileImage = new Image(IconBundle.class.getResourceAsStream("icons/tree/image-x-generic.png"));
  public final Image treeFileAudioIcon = new Image(IconBundle.class.getResourceAsStream("icons/tree/audio-x-generic.png"));
  public final Image treeFileVideoIcon = new Image(IconBundle.class.getResourceAsStream("icons/tree/video-x-generic.png"));
  public final Image treeFileExecIcon = new Image(IconBundle.class.getResourceAsStream("icons/tree/application-x-executable.png"));
  public final Image treeFileHtmlIcon = new Image(IconBundle.class.getResourceAsStream("icons/tree/text-html.png"));
  public final Image treeFileScriptIcon = new Image(IconBundle.class.getResourceAsStream("icons/tree/text-x-script.png"));

  private IconBundle() {
  }

  public static IconBundle get() {
    if (instance == null) {
      instance = new IconBundle();
    }
    return instance;
  }
}
