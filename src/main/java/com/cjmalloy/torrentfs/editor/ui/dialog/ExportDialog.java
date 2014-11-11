package com.cjmalloy.torrentfs.editor.ui.dialog;

import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.cjmalloy.torrentfs.editor.event.ExportEvent;
import com.cjmalloy.torrentfs.editor.ui.component.ExportSettingsComponent;
import com.google.common.eventbus.Subscribe;


public class ExportDialog extends Dialog
{
    private static final ResourceBundle R = ResourceBundle.getBundle("com.cjmalloy.torrentfs.editor.i18n.MessageBundle");

    private JDialog dialog;
    private ExportSettingsComponent exportSettings = new ExportSettingsComponent();

    public ExportDialog(JFrame parent)
    {
        super(parent);

        dialog = new JDialog(parent, R.getString("exportDialogTitle"), true);
        dialog.add(exportSettings.getLayout());
    }

    @Subscribe
    public void export(ExportEvent event)
    {
        dialog.setVisible(true);
        event.callback.withSettings(exportSettings.getValue());
    }
}
