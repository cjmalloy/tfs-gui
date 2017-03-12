package com.cjmalloy.torrentfs.editor.ui.swing.component;

import java.awt.Component;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.cjmalloy.torrentfs.editor.model.ExportSettings;


public class ExportSettingsComponent implements SettingsComponent<ExportSettings> {
  private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

  private JPanel widget;
  private DirSelector torrentSaveDir;
  private JPanel trackers;
  private JTextArea trackersInput;
  private JPanel cacheArea;
  private JCheckBox initCache;
  private DirSelector cacheDir;
  private JCheckBox useLinks;

  private ExportSettings value;

  public ExportSettingsComponent() {
    load(new ExportSettings());
  }

  @Override
  public ExportSettings getValue() {
    saveToModel();
    return value;
  }

  @Override
  public Component getWidget() {
    if (widget == null) {
      widget = new JPanel();
      widget.setLayout(new BoxLayout(widget, BoxLayout.PAGE_AXIS));
      widget.add(getTorrentSaveDir().getWidget());
      widget.add(getTrackers());
      widget.add(getCacheArea());
    }
    return widget;
  }

  @Override
  public ExportSettingsComponent load(ExportSettings settings) {
    value = settings;
    loadModel();
    return this;
  }

  private JPanel getCacheArea() {
    if (cacheArea == null) {
      cacheArea = new JPanel();
      cacheArea.setBorder(new TitledBorder(R.getString("cacheAreaLabel")));
      cacheArea.setLayout(new BoxLayout(cacheArea, BoxLayout.PAGE_AXIS));
      cacheArea.add(getInitCache());
      cacheArea.add(getCacheDir().getWidget());
      cacheArea.add(getUseLinks());
    }
    return cacheArea;
  }

  private DirSelector getCacheDir() {
    if (cacheDir == null) {
      cacheDir = new DirSelector(R.getString("cacheDirLabel"));
    }
    return cacheDir;
  }

  private JCheckBox getInitCache() {
    if (initCache == null) {
      initCache = new JCheckBox(R.getString("initCacheCheckbox"));
      initCache.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          getCacheDir().getWidget().setEnabled(getInitCache().isSelected());
          getUseLinks().setEnabled(getInitCache().isSelected());
        }
      });
    }
    return initCache;
  }

  private DirSelector getTorrentSaveDir() {
    if (torrentSaveDir == null) {
      torrentSaveDir = new DirSelector(R.getString("torrentSaveDirLabel"));
    }
    return torrentSaveDir;
  }

  private JPanel getTrackers() {
    if (trackers == null) {
      trackers = new JPanel();
      trackers.setLayout(new BoxLayout(trackers, BoxLayout.PAGE_AXIS));
      trackers.add(new Label(R.getString("torrentTrackersLabel")));
      trackers.add(new JScrollPane(getTrackersInput()));
    }
    return trackers;
  }

  private JTextArea getTrackersInput() {
    if (trackersInput == null) {
      trackersInput = new JTextArea();

    }
    return trackersInput;
  }

  private JCheckBox getUseLinks() {
    if (useLinks == null) {
      useLinks = new JCheckBox(R.getString("getUseLinksCheckbox"));
    }
    return useLinks;
  }

  private void loadModel() {
    try {
      getTorrentSaveDir().setText(value.torrentSaveDir.toString());
    } catch (Exception e) {
      getTorrentSaveDir().setText("");
    }
    String trackersText = "";
    if (value.announce != null) {
      for (String uri : value.announce) {
        trackersText += uri + "\n";
      }
    }
    getTrackersInput().setText(trackersText);
    getInitCache().setSelected(value.cache != null);
    getUseLinks().setSelected(value.useLinks);
    getCacheDir().getWidget().setEnabled(value.cache != null);
    getUseLinks().setEnabled(value.cache != null);
    if (value.cache == null) {
      getCacheDir().setText("");
    } else {
      getCacheDir().setText(value.cache.toString());
    }
  }

  private void saveToModel() {
    value.torrentSaveDir = new File(getTorrentSaveDir().getText());
    if (getTrackersInput().getText().length() == 0) {
      value.announce = null;
    } else {
      value.announce = Arrays.asList(getTrackersInput().getText().split("\n"));
    }
    if (getInitCache().isSelected()) {
      value.cache = new File(getCacheDir().getText());
    } else {
      value.cache = null;
    }
    value.useLinks = getUseLinks().isSelected();
  }
}
