// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GaussianWindowTest extends TestCase {
  public void testSimple() {
    Scalar apply = GaussianWindow.FUNCTION.apply(RealScalar.of(.2));
    Scalar exact = RealScalar.of(0.80073740291680804078);
    assertTrue(Chop._10.close(apply, exact));
  }
}
