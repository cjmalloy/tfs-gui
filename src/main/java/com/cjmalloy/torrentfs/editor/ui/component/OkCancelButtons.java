package com.cjmalloy.torrentfs.editor.ui.component;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.cjmalloy.torrentfs.editor.ui.HasWidget;
import com.cjmalloy.torrentfs.editor.ui.layoutmanager.ButtonAreaLayout;


public class OkCancelButtons implements HasWidget
{

    private static final Insets BUTTON_INSETS = new Insets(2, 8, 2, 8);

    private JPanel widget;
    private JButton okButton;
    private JButton cancelButton;

    private OkCancelDelegate delegate;

    public OkCancelButtons() {}

    public OkCancelButtons(OkCancelDelegate delegate)
    {
        setDelegate(delegate);
    }

    @Override
    public JPanel getWidget()
    {
        if (widget == null)
        {
            widget = new JPanel();
            widget.setLayout(new ButtonAreaLayout(true, 6, SwingConstants.RIGHT, false));
            widget.setBorder(UIManager.getBorder("OptionPane.buttonAreaBorder"));
            widget.add(getOkButton());
            widget.add(getCancelButton());
        }
        return widget;
    }

    public void setDelegate(OkCancelDelegate delegate)
    {
        this.delegate = delegate;
    }

    private JButton getCancelButton()
    {
        if (cancelButton == null)
        {
            cancelButton = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
            cancelButton.setMargin(BUTTON_INSETS);
            cancelButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    delegate.onCancel();
                }
            });
        }
        return cancelButton;
    }

    private JButton getOkButton()
    {
        if (cancelButton == null)
        {
            okButton = new JButton(UIManager.getString("OptionPane.okButtonText"));
            okButton.setMargin(BUTTON_INSETS);
            okButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    delegate.onOk();
                }
            });
        }
        return okButton;
    }

    public static interface OkCancelDelegate
    {
        void onCancel();
        void onOk();
    }

}
