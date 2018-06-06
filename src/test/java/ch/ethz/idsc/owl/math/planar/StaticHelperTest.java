// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.UnitVector;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    Scalar det = StaticHelper.det(UnitVector.of(2, 0), UnitVector.of(2, 1));
    assertEquals(det, RealScalar.ONE);
    assertTrue(ExactScalarQ.of(det));
  }
}
