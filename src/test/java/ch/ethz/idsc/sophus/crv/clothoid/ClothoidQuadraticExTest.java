// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class ClothoidQuadraticExTest extends TestCase {
  public void testSimple() {
    ClothoidQuadraticEx clothoidQuadratic = //
        new ClothoidQuadraticEx(RealScalar.of(2), RealScalar.of(-3), RealScalar.of(7));
    Scalar p0 = clothoidQuadratic.angle(RealScalar.ZERO);
    Scalar pm = clothoidQuadratic.angle(RationalScalar.of(1, 2));
    Scalar p1 = clothoidQuadratic.angle(RealScalar.ONE);
    assertEquals(p0, RealScalar.of(2));
    assertEquals(pm, RealScalar.of(-3));
    assertEquals(p1, RealScalar.of(7));
  }

  public void testExamples() {
    ClothoidQuadraticEx clothoidQuadratic = //
        new ClothoidQuadraticEx(RealScalar.of(5), RealScalar.of(7), RealScalar.of(13));
    Scalar angle = clothoidQuadratic.angle(RealScalar.of(11));
    assertEquals(ExactScalarQ.require(angle), RealScalar.of(973));
  }
}
