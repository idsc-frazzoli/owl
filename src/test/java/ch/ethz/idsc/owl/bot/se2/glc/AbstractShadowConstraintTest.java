// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class AbstractShadowConstraintTest extends TestCase {
  public void testNumeric() {
    Tensor tensor = AbstractShadowConstraint.DIR;
    assertFalse(ExactScalarQ.of(tensor.Get(0)));
    assertFalse(ExactScalarQ.of(tensor.Get(1)));
  }
}
