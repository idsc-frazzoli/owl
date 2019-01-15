// code by jph
package ch.ethz.idsc.sophus.dubins;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class FixedRadiusDubinsTest extends TestCase {
  public void testTest() {
    DubinsPathGenerator dubinsPathGenerator = FixedRadiusDubins.of(Tensors.vector(10, 2, Math.PI / 2), RealScalar.of(1));
    assertEquals(dubinsPathGenerator.allValid().count(), 4);
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    DubinsPathGenerator dubinsPathGenerator = FixedRadiusDubins.of(Tensors.vector(10, 2, Math.PI / 2), RealScalar.of(1));
    DubinsPathGenerator copy = Serialization.copy(dubinsPathGenerator);
    assertEquals(copy.allValid().count(), 4);
  }

  public void testMin2() {
    for (int count = 0; count < 100; ++count) {
      Tensor tensor = RandomVariate.of(NormalDistribution.standard(), 3);
      DubinsPathGenerator dubinsPathGenerator = FixedRadiusDubins.of(tensor, RealScalar.of(.1));
      assertTrue(2 <= dubinsPathGenerator.allValid().count());
    }
  }
}
