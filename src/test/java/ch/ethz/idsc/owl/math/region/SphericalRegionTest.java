// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SphericalRegionTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = new SphericalRegion(Tensors.vector(1, 1), RealScalar.ONE);
    assertTrue(region.isMember(Tensors.vector(1, 0)));
    assertTrue(region.isMember(Tensors.vector(0, 1)));
    assertFalse(region.isMember(Tensors.vector(2, 0)));
    assertFalse(region.isMember(Tensors.vector(0, 2)));
  }

  public void testPoint() {
    Region<Tensor> region = new SphericalRegion(Tensors.vector(1, 1), RealScalar.ZERO);
    assertTrue(region.isMember(Tensors.vector(1, 1)));
  }

  public void testDistance() {
    SphericalRegion region = new SphericalRegion(Tensors.vector(1, 1), RealScalar.ZERO);
    assertEquals(region.apply(Tensors.vector(11, 1)), RealScalar.of(10));
  }

  public void testFail() {
    try {
      new SphericalRegion(RealScalar.ZERO, RealScalar.ONE);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
