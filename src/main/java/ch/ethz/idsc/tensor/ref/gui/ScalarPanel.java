// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.StringScalarQ;

/* package */ class ScalarPanel extends StringPanel {
  private static final Color FAIL = new Color(255, 192, 192);

  public ScalarPanel(Tensor tensor) {
    super(tensor.toString());
    jTextField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent keyEvent) {
        Scalar scalar = Scalars.fromString(getText());
        boolean isOk = !StringScalarQ.of(scalar);
        jTextField.setBackground(isOk ? Color.WHITE : FAIL);
      }
    });
  }
}
