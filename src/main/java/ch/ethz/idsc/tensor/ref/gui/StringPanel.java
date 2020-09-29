// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JTextField;

/* package */ class StringPanel implements FieldPanel {
  private static final Font FONT = new Font(Font.DIALOG_INPUT, Font.PLAIN, 18);
  final JTextField jTextField;

  public StringPanel(String string) {
    jTextField = new JTextField(string);
    jTextField.setFont(FONT);
  }

  @Override
  public JComponent getComponent() {
    return jTextField;
  }

  @Override
  public String getText() {
    return jTextField.getText();
  }
}
