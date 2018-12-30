// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class FresnelCTest extends TestCase {
  public void testSimple() {
    Scalar scalar = FresnelC.FUNCTION.apply(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ZERO);
  }

  public void testOneP() {
    Scalar scalar = FresnelC.FUNCTION.apply(RealScalar.ONE);
    assertTrue(Chop._12.close(scalar, RealScalar.of(+0.904524237900272)));
  }

  public void testOneN() {
    Scalar scalar = FresnelC.FUNCTION.apply(RealScalar.ONE.negate());
    assertTrue(Chop._12.close(scalar, RealScalar.of(-0.904524237900272)));
  }
}
