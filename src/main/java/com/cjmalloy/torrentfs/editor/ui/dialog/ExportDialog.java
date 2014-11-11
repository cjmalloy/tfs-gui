package com.cjmalloy.torrentfs.editor.ui.dialog;

import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.cjmalloy.torrentfs.editor.event.ExportEvent;
import com.cjmalloy.torrentfs.editor.model.ExportSettings;
import com.cjmalloy.torrentfs.editor.ui.component.ExportSettingsComponent;
import com.google.common.eventbus.Subscribe;


public class ExportDialog extends Dialog
{
    private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

    private SettingsDialog<ExportSettings> dialog;
    private ExportSettingsComponent exportSettings = new ExportSettingsComponent();

    public ExportDialog(JFrame parent)
    {
        super(parent);
        dialog = new SettingsDialog<>(parent, R.getString("exportDialogTitle"), exportSettings);
    }

    @Subscribe
    public void export(final ExportEvent event)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                int response = dialog.showSettingsDialog();
                if (response == SettingsDialog.OK_OPTION)
                {
                    event.callback.withSettings(exportSettings.getValue());
                }
            }
        });
    }
}
