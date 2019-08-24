// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ClothoidApproximationTest extends TestCase {
  public void testSimple() {
    Scalar f = ClothoidApproximation.f(RealScalar.of(0.3), RealScalar.of(-0.82));
    Chop._15.requireClose(f, RealScalar.of(0.1213890127877238));
  }
  // public void test2Pi() {
  // Scalar f = ClothoidApproximation.f(RealScalar.of(0.3+2*Math.PI), RealScalar.of(-0.82+2*Math.PI));
  // System.out.println(f);
  // }
}
