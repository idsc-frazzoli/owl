// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class ClothoidLR3MidpointTest extends TestCase {
  public void testSimple() {
    Tensor midpoint = ClothoidLR3Midpoint.INSTANCE.midpoint(Tensors.vector(1, 2, 0), Tensors.vector(9, 2, 0));
    assertEquals(midpoint, Tensors.vector(5, 2, 0));
  }

  public void testDifference() {
    Distribution distribution = NormalDistribution.standard();
    Tensor p = RandomVariate.of(distribution, 3);
    Tensor q = RandomVariate.of(distribution, 3);
    Tensor lr1 = ClothoidLR1Midpoint.INSTANCE.midpoint(p, q);
    Tensor lr3 = ClothoidLR3Midpoint.INSTANCE.midpoint(p, q);
    Norm._2.between(lr1, lr3);
    // System.out.println(scalar);
  }
}
