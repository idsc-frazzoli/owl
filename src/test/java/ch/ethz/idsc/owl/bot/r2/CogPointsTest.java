// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.util.Arrays;

import ch.ethz.idsc.sophus.lie.r2.ConvexHull;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class CogPointsTest extends TestCase {
  public void testSimple() {
    Tensor tensor = CogPoints.of(10, RealScalar.of(10.2), RealScalar.of(3.2));
    assertEquals(Dimensions.of(tensor), Arrays.asList(40, 2));
    Tensor convex = ConvexHull.of(tensor);
    assertEquals(Dimensions.of(convex), Arrays.asList(20, 2));
  }

  public void testToggled() {
    Tensor tensor = CogPoints.of(10, RealScalar.of(10.2), RealScalar.of(30.2));
    assertEquals(Dimensions.of(tensor), Arrays.asList(40, 2));
    Tensor convex = ConvexHull.of(tensor);
    assertEquals(Dimensions.of(convex), Arrays.asList(20, 2));
  }
}
