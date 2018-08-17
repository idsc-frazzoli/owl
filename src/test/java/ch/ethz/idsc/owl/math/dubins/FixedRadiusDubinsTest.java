// code by jph
package ch.ethz.idsc.owl.math.dubins;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class FixedRadiusDubinsTest extends TestCase {
  public void testTest() {
    FixedRadiusDubins fixedRadiusDubins = new FixedRadiusDubins(Tensors.vector(10, 2, Math.PI / 2), RealScalar.of(1));
    assertEquals(fixedRadiusDubins.allValid().count(), 4);
  }

  public void testMin2() {
    for (int count = 0; count < 100; ++count) {
      Tensor tensor = RandomVariate.of(NormalDistribution.standard(), 3);
      FixedRadiusDubins fixedRadiusDubins = new FixedRadiusDubins(tensor, RealScalar.of(.1));
      assertTrue(2 <= fixedRadiusDubins.allValid().count());
    }
  }
}
