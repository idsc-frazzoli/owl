// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LieGroupGeodesicTest extends TestCase {
  public void testSe2() {
    LieGroupGeodesic lieGroupGeodesic = //
        new LieGroupGeodesic(Se2CoveringGroup.INSTANCE::element, Se2CoveringExponential.INSTANCE);
    Tensor p = Tensors.vector(1, 2, 3);
    Tensor q = Tensors.vector(4, 5, 6);
    Scalar lambda = RealScalar.of(0.7);
    Tensor tensor = lieGroupGeodesic.split(p, q, lambda);
    Tensor split = Se2CoveringGeodesic.INSTANCE.split(p, q, lambda);
    assertEquals(tensor, split);
  }
}
