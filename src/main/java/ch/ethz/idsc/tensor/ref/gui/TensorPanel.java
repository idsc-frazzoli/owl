// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.StringScalarQ;

/* package */ class TensorPanel extends StringPanel {
  private static final Color FAIL = new Color(255, 192, 192);

  public TensorPanel(Tensor tensor) {
    super(tensor.toString());
    jTextField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent keyEvent) {
        Tensor tensor = Tensors.fromString(getText());
        boolean isOk = !StringScalarQ.any(tensor);
        jTextField.setBackground(isOk ? Color.WHITE : FAIL);
      }
    });
  }
}
