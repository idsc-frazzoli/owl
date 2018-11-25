// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class FresnelSTest extends TestCase {
  public void testSimple() {
    Scalar scalar = FresnelS.FUNCTION.apply(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ZERO);
  }
}
