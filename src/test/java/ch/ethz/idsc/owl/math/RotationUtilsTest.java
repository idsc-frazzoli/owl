// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Degree;
import junit.framework.TestCase;

public class RotationUtilsTest extends TestCase {
  public void testSimple() {
    Scalar s1 = RationalScalar.of(7, 180).multiply(DoubleScalar.of(Math.PI));
    Scalar s2 = Degree.of(7);
    assertEquals(s1, s2);
  }
}
