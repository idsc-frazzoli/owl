// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class FresnelSTest extends TestCase {
  public void testSimple() {
    Scalar scalar = FresnelS.FUNCTION.apply(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ZERO);
  }

  public void testOneP() {
    Scalar scalar = FresnelS.FUNCTION.apply(RealScalar.ONE);
    assertTrue(Chop._12.close(scalar, RealScalar.of(+0.3102683017233811)));
  }

  public void testOneN() {
    Scalar scalar = FresnelS.FUNCTION.apply(RealScalar.ONE.negate());
    assertTrue(Chop._12.close(scalar, RealScalar.of(-0.3102683017233811)));
  }
}
