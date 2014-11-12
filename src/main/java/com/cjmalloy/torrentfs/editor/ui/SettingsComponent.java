package com.cjmalloy.torrentfs.editor.ui;


public interface SettingsComponent<T> extends HasWidget
{
    T getValue();
    void load(T settings);
}
