// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ClothoidCurveTest extends TestCase {
  private static final Unit METER = Unit.of("m");

  public static Tensor metric(Tensor vector) {
    return Tensors.of( //
        Quantity.of(vector.Get(0), METER), //
        Quantity.of(vector.Get(1), METER), //
        vector.Get(2));
  }

  public void testComparison() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor p = RandomVariate.of(distribution, 3);
      Tensor q = RandomVariate.of(distribution, 3);
      Scalar lambda = RandomVariate.of(distribution);
      Tensor r1 = new ClothoidCurve2(p, q).apply(lambda);
      Tensor r2 = new ClothoidCurve3(p, q).apply(lambda);
      Chop._14.requireClose(r1, r2);
    }
  }
}
