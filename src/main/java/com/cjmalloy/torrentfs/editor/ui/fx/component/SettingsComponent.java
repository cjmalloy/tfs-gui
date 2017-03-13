package com.cjmalloy.torrentfs.editor.ui.fx.component;

import java.io.IOException;

import com.cjmalloy.torrentfs.editor.model.ExportSettings;
import com.cjmalloy.torrentfs.editor.ui.swing.HasWidget;


public interface SettingsComponent<T> extends HasWidget {
  T getValue();

  SettingsComponent<T> load(T settings);

  public static class SettingsComponentFactory {
    public static <T> SettingsComponent<T> create(T settings) throws IOException {
      if (settings instanceof ExportSettings) return load(new ExportSettingsComponent(), settings);
      //TODO:
      return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> SettingsComponent<T> load(SettingsComponent<?> c, T settings) {
      return ((SettingsComponent<T>) c).load(settings);
    }
  }
}
