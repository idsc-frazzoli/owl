// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.io.StringScalarQ;
import ch.ethz.idsc.tensor.ref.FieldClip;
import ch.ethz.idsc.tensor.ref.FieldIntegerQ;
import ch.ethz.idsc.tensor.ref.TensorReflection;
import ch.ethz.idsc.tensor.sca.Clip;

/* package */ class ScalarPanel extends StringPanel {
  private static final Color FAIL = new Color(255, 192, 192);
  private final boolean requireInteger;
  private final Optional<Clip> optionalClip;

  public ScalarPanel(Field field, Scalar scalar) {
    super(scalar.toString());
    requireInteger = Objects.nonNull(field.getAnnotation(FieldIntegerQ.class));
    optionalClip = TensorReflection.of(field.getAnnotation(FieldClip.class));
    jTextField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent keyEvent) {
        boolean isOk = isOk();
        if (isOk)
          jTextField.setToolTipText(null);
        jTextField.setBackground(isOk ? Color.WHITE : FAIL);
      }
    });
  }

  public boolean isOk() {
    Scalar scalar = Scalars.fromString(getText());
    boolean isOk = !StringScalarQ.of(scalar);
    if (!isOk)
      jTextField.setToolTipText("not a valid scalar");
    else {
      isOk = !requireInteger || IntegerQ.of(scalar);
      if (!isOk)
        jTextField.setToolTipText("integer is required");
      else {
        isOk = false;
        try {
          isOk = !optionalClip.isPresent() || optionalClip.get().isInside(scalar);
        } catch (Exception exception) {
          // ---
        }
        if (!isOk)
          jTextField.setToolTipText("scalar outside interval " + optionalClip.get());
      }
    }
    return isOk;
  }
}
