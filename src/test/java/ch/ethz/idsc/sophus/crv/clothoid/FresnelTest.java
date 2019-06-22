// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class FresnelTest extends TestCase {
  public void testCSimple() {
    Scalar scalar = Fresnel.C().apply(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ZERO);
  }

  public void testCOneP() {
    Scalar scalar = Fresnel.C().apply(RealScalar.ONE);
    assertTrue(Chop._12.close(scalar, RealScalar.of(+0.904524237900272)));
  }

  public void testCOneN() {
    Scalar scalar = Fresnel.C().apply(RealScalar.ONE.negate());
    assertTrue(Chop._12.close(scalar, RealScalar.of(-0.904524237900272)));
  }

  public void testSSimple() {
    Scalar scalar = Fresnel.S().apply(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ZERO);
  }

  public void testSOneP() {
    Scalar scalar = Fresnel.S().apply(RealScalar.ONE);
    assertTrue(Chop._12.close(scalar, RealScalar.of(+0.3102683017233811)));
  }

  public void testSOneN() {
    Scalar scalar = Fresnel.S().apply(RealScalar.ONE.negate());
    assertTrue(Chop._12.close(scalar, RealScalar.of(-0.3102683017233811)));
  }
}
