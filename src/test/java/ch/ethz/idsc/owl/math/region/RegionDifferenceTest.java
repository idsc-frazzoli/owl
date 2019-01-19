// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RegionDifferenceTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = RegionDifference.of( //
        new SphericalRegion(Tensors.vector(0, 0), RealScalar.ONE), //
        new SphericalRegion(Tensors.vector(1, 0), RealScalar.ONE));
    assertTrue(region.isMember(Tensors.vector(-0.5, 0)));
    assertFalse(region.isMember(Tensors.vector(0.5, 0)));
    assertFalse(region.isMember(Tensors.vector(2.5, 0)));
  }
}
