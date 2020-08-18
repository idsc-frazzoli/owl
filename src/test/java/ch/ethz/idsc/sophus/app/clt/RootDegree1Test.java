// code by jph
package ch.ethz.idsc.sophus.app.clt;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import junit.framework.TestCase;

public class RootDegree1Test extends TestCase {
  public void testSimple() {
    Scalar scalar = RootDegree1.of(RealScalar.of(10), RealScalar.of(11), RealScalar.of(5), RealScalar.of(-2));
    assertEquals(scalar, RationalScalar.of(75, 7));
  }
}
