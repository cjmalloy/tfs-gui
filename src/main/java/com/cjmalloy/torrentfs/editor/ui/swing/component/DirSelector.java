package com.cjmalloy.torrentfs.editor.ui.swing.component;

import com.cjmalloy.torrentfs.editor.controller.Controller;
import com.cjmalloy.torrentfs.editor.event.DoOpenFolder;
import com.cjmalloy.torrentfs.editor.event.DoOpenFolder.OpenFolderCallback;
import com.cjmalloy.torrentfs.editor.ui.swing.HasWidget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;


public class DirSelector implements HasWidget {
  private static final Dimension INPUT_SIZE = new Dimension(300, 30);

  private JPanel widget;
  private JTextField dirInput;
  private JButton dirBrowse;

  private String label;

  public DirSelector(String label) {
    this.label = label;
  }

  public String getText() {
    return getDirInput().getText();
  }

  @Override
  public Component getWidget() {
    if (widget == null) {
      widget = new JPanel();
      widget.setLayout(new BoxLayout(widget, BoxLayout.LINE_AXIS));
      widget.add(new JLabel(label));
      widget.add(getDirInput());
      widget.add(getDirBrowse());
    }
    return widget;
  }

  public void setText(String text) {
    getDirInput().setText(text);
  }

  private JButton getDirBrowse() {
    if (dirBrowse == null) {
      dirBrowse = new JButton("...");
      dirBrowse.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          Controller.EVENT_BUS.post(new DoOpenFolder(new OpenFolderCallback() {
            @Override
            public void withFolder(Path folder) {
              getDirInput().setText(folder.toString());
            }
          }));
        }
      });
    }
    return dirBrowse;
  }

  private JTextField getDirInput() {
    if (dirInput == null) {
      dirInput = new JTextField();
      dirInput.setPreferredSize(INPUT_SIZE);
    }
    return dirInput;
  }
}
