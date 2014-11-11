package com.cjmalloy.torrentfs.editor.ui;


public interface SettingsComponent<T> extends HasLayout
{
    T getValue();
    void load(T settings);
}
