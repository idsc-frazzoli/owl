// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import ch.ethz.idsc.tensor.ref.FieldType;

/* package */ class FilePanel extends StringPanel {
  private final JButton jButton = new JButton("?");

  public FilePanel(Field field, FieldType fieldType, File file) {
    super(field, fieldType, file);
    jButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser jFileChooser = new JFileChooser(file);
        jFileChooser.setBounds(100, 100, 600, 600);
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int openDialog = jFileChooser.showOpenDialog(null);
        if (openDialog == JFileChooser.APPROVE_OPTION)
          jTextField.setText(jFileChooser.getSelectedFile().toString());
      }
    });
  }

  @Override
  public JComponent getComponent() {
    JPanel jPanel = new JPanel(new BorderLayout());
    jPanel.add("Center", jTextField);
    jPanel.add("East", jButton);
    return jPanel;
  }
}
