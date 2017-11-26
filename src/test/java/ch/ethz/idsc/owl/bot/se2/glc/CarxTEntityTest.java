// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class CarxTEntityTest extends TestCase {
  public void testSimple() {
    CarxTEntity carxt = new CarxTEntity(new StateTime(Tensors.vector(1, 2, 3), RealScalar.ZERO));
    Tensor eta = carxt.eta();
    assertTrue(ExactScalarQ.of(eta.Get(3)));
  }
}
