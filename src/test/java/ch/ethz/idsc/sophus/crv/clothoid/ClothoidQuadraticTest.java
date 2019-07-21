// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class ClothoidQuadraticTest extends TestCase {
  public void testSimple() {
    ClothoidQuadratic clothoidQuadratic = //
        new ClothoidQuadratic(RealScalar.of(2), RealScalar.of(-3), RealScalar.of(7));
    Scalar p0 = clothoidQuadratic.apply(RealScalar.ZERO);
    Scalar pm = clothoidQuadratic.apply(RationalScalar.HALF);
    Scalar p1 = clothoidQuadratic.apply(RealScalar.ONE);
    assertEquals(p0, RealScalar.of(2));
    assertEquals(pm, RealScalar.of(-3));
    assertEquals(p1, RealScalar.of(7));
  }

  public void testExamples() {
    ClothoidQuadratic clothoidQuadratic = //
        new ClothoidQuadratic(RealScalar.of(5), RealScalar.of(7), RealScalar.of(13));
    Scalar angle = clothoidQuadratic.apply(RealScalar.of(11));
    assertEquals(ExactScalarQ.require(angle), RealScalar.of(973));
    Scalar p0 = clothoidQuadratic.apply(RealScalar.ZERO);
    Scalar pm = clothoidQuadratic.apply(RationalScalar.HALF);
    Scalar p1 = clothoidQuadratic.apply(RealScalar.ONE);
    assertEquals(p0, RealScalar.of(5));
    assertEquals(pm, RealScalar.of(7));
    assertEquals(p1, RealScalar.of(13));
  }
}
