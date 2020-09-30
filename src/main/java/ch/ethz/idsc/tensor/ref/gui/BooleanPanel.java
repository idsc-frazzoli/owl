// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

/* package */ class BooleanPanel extends FieldPanel {
  private final JCheckBox jCheckBox;

  public BooleanPanel(Boolean value) {
    jCheckBox = new JCheckBox();
    if (Objects.nonNull(value))
      jCheckBox.setSelected(value);
    jCheckBox.addActionListener(event -> notifyListeners(getText()));
  }

  @Override
  public JComponent getComponent() {
    return jCheckBox;
  }

  private String getText() {
    return "" + jCheckBox.isSelected();
  }
}
