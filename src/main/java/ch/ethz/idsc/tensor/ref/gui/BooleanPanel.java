// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

/* package */ class BooleanPanel implements FieldPanel {
  private final JCheckBox jCheckBox;

  public BooleanPanel(Boolean value) {
    jCheckBox = new JCheckBox();
    if (Objects.nonNull(value))
      jCheckBox.setSelected(value);
  }

  @Override
  public JComponent getComponent() {
    return jCheckBox;
  }

  @Override
  public String getText() {
    return "" + jCheckBox.isSelected();
  }
}
