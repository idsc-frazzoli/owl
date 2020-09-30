// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

import javax.swing.JComponent;
import javax.swing.JTextField;

import ch.ethz.idsc.tensor.ref.FieldType;

/* package */ class StringPanel extends FieldPanel {
  private static final Color LABEL = new Color(51, 51, 51);
  // ---
  private final Field field;
  private final FieldType fieldType;
  protected final JTextField jTextField;

  public StringPanel(Field field, FieldType fieldType, Object object) {
    this.field = field;
    this.fieldType = fieldType;
    jTextField = new JTextField(object.toString());
    jTextField.setFont(FieldPanel.FONT);
    jTextField.setForeground(LABEL);
    jTextField.addActionListener(l -> notifyListeners(jTextField.getText()));
    jTextField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent keyEvent) {
        indicateGui();
      }
    });
    indicateGui();
  }

  @Override
  public JComponent getComponent() {
    return jTextField;
  }

  private void indicateGui() {
    boolean isOk = fieldType.isValidString(field, jTextField.getText());
    jTextField.setBackground(isOk ? Color.WHITE : FAIL);
  }
}
