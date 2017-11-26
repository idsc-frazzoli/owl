// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class EllipsePointsTest extends TestCase {
  public void testScaled() {
    int n = 11;
    Tensor tensor = EllipsePoints.of(n, RealScalar.of(2), RealScalar.of(.5));
    // System.out.println(Pretty.of(tensor.map(Round._4)));
    assertEquals(Dimensions.of(tensor), Arrays.asList(n, 2));
  }
}
