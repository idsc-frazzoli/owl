// code by jph
package ch.ethz.idsc.sophus.lie.se2c;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CoveringIntegratorTest extends TestCase {
  public void testFullRotation() {
    Tensor g = Tensors.vector(10, 0, 0).unmodifiable();
    for (Tensor _x : Subdivide.of(-2, 10, 72)) {
      Tensor x = Tensors.vector(_x.Get().number().doubleValue(), 0, 2 * Math.PI);
      Tensor r = Se2CoveringIntegrator.INSTANCE.spin(g, x);
      assertTrue(Chop._12.close(r, Tensors.vector(10.0, 0.0, 6.283185307179586)));
    }
  }
}
