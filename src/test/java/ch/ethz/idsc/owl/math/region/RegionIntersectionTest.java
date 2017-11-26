// code by jph
package ch.ethz.idsc.owl.math.region;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RegionIntersectionTest extends TestCase {
  public void testSimple() {
    Region<Tensor> intersection = RegionIntersection.wrap( //
        Arrays.asList( //
            new EllipsoidRegion(Tensors.vector(-2), Tensors.vector(3)), //
            new EllipsoidRegion(Tensors.vector(+2), Tensors.vector(3))));
    assertTrue(intersection.isMember(Tensors.vector(0)));
    assertTrue(intersection.isMember(Tensors.vector(0.5)));
    assertTrue(intersection.isMember(Tensors.vector(1)));
    assertFalse(intersection.isMember(Tensors.vector(1.2)));
    assertFalse(intersection.isMember(Tensors.vector(-1.2)));
  }
}
