// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JTextField;

/* package */ class StringPanel extends FieldPanel {
  static final Font FONT = new Font(Font.DIALOG_INPUT, Font.PLAIN, 18);
  public static final Color LABEL = new Color(51, 51, 51);
  final JTextField jTextField;

  public StringPanel(String string) {
    jTextField = new JTextField(string);
    jTextField.setFont(FONT);
    jTextField.setForeground(LABEL);
    jTextField.addActionListener(l -> notifyListeners(jTextField.getText()));
  }

  @Override
  public JComponent getComponent() {
    return jTextField;
  }
}
