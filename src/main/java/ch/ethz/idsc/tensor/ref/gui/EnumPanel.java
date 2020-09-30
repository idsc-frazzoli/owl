// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ch.ethz.idsc.java.awt.SpinnerLabel;

/* package */ class EnumPanel extends FieldPanel {
  private final SpinnerLabel<Object> spinnerLabel = new SpinnerLabel<>();

  public EnumPanel(Object[] objects, Object object) {
    JLabel jLabel = spinnerLabel.getLabelComponent();//
    System.out.println(jLabel.getForeground());
    jLabel.setFont(StringPanel.FONT);
    jLabel.setHorizontalAlignment(SwingConstants.LEFT);
    spinnerLabel.setArray(objects);
    spinnerLabel.setValueSafe(object);
    spinnerLabel.addSpinnerListener(value -> notifyListeners(value.toString()));
  }

  @Override
  public JComponent getComponent() {
    return spinnerLabel.getLabelComponent();
  }
}
