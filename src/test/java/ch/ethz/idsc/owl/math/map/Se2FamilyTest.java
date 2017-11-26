// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2FamilyTest extends TestCase {
  public void testSimple() {
    BijectionFamily bijectionFamily = new Se2Family(s -> Tensors.of( //
        RealScalar.of(2).add(s), //
        RealScalar.of(1).multiply(s), RealScalar.of(5).subtract(s) //
    ));
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 100; ++index) {
      Scalar scalar = RandomVariate.of(distribution);
      Tensor point = RandomVariate.of(distribution, 2);
      Tensor fwd = bijectionFamily.forward(scalar).apply(point);
      assertTrue(Chop._12.close(bijectionFamily.inverse(scalar).apply(fwd), point));
    }
  }

  public void testReverse() {
    BijectionFamily bijectionFamily = new Se2Family(s -> Tensors.of( //
        RealScalar.of(2).add(s), //
        RealScalar.of(1).multiply(s), RealScalar.of(5).subtract(s) //
    ));
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 100; ++index) {
      Scalar scalar = RandomVariate.of(distribution);
      Tensor point = RandomVariate.of(distribution, 2);
      Tensor fwd = bijectionFamily.inverse(scalar).apply(point);
      assertTrue(Chop._12.close(bijectionFamily.forward(scalar).apply(fwd), point));
    }
  }
}
