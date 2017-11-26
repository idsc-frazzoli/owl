// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class HyperplaneRegionTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = new HyperplaneRegion(Tensors.vector(1, 0), RealScalar.of(5));
    assertFalse(region.isMember(Tensors.vector(0, 0)));
    assertFalse(region.isMember(Tensors.vector(3, 0)));
    assertFalse(region.isMember(Tensors.vector(5, 0)));
    assertFalse(region.isMember(Tensors.vector(8, 0)));
    assertFalse(region.isMember(Tensors.vector(-3, 0)));
    assertTrue(region.isMember(Tensors.vector(-5, 0)));
    assertTrue(region.isMember(Tensors.vector(-8, 0)));
  }
}
