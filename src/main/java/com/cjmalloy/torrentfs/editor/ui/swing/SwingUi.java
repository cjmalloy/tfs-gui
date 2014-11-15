package com.cjmalloy.torrentfs.editor.ui.swing;

import com.cjmalloy.torrentfs.editor.model.ExportSettings;
import com.cjmalloy.torrentfs.editor.ui.TopLevel;
import com.cjmalloy.torrentfs.editor.ui.swing.component.ExportSettingsComponent;
import com.cjmalloy.torrentfs.editor.ui.swing.component.SettingsComponent;
import com.cjmalloy.torrentfs.editor.ui.swing.toplevel.Window;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;


public class SwingUi extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(TopLevel.class).to(Window.class);
        
        bind(new TypeLiteral<SettingsComponent<ExportSettings>>(){})
            .to(ExportSettingsComponent.class);

    }

}
