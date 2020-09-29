// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import javax.swing.JComponent;

import ch.ethz.idsc.java.awt.SpinnerLabel;

/* package */ class EnumPanel implements FieldPanel {
  // Object[] objects = ;
  private final SpinnerLabel spinnerLabel = new SpinnerLabel();

  // s.setArray(objects);
  // s.setValueSafe(value);
  // JToolBar jToolBar = createRow(field.getName(), BUTTON + 2);
  // s.addToComponentReduced(jToolBar, new Dimension(200, 20), "tooltip");
  public EnumPanel(Object[] objects, Object object) {
    spinnerLabel.setArray(objects);
    spinnerLabel.setValueSafe(object);
  }

  @Override
  public JComponent getComponent() {
    return spinnerLabel.getLabelComponent();
  }

  @Override
  public String getText() {
    return spinnerLabel.getValue().toString();
  }
}
