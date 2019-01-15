// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class So2FamilyTest extends TestCase {
  public void testSimple() {
    BijectionFamily bijectionFamily = new So2Family(s -> RealScalar.of(5).subtract(s));
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 100; ++index) {
      Scalar scalar = RandomVariate.of(distribution);
      Tensor point = RandomVariate.of(distribution, 2);
      Tensor fwd = bijectionFamily.forward(scalar).apply(point);
      assertTrue(Chop._12.close(bijectionFamily.inverse(scalar).apply(fwd), point));
    }
  }

  public void testReverse() {
    BijectionFamily bijectionFamily = new So2Family(s -> RealScalar.of(1.2).multiply(s));
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 100; ++index) {
      Scalar scalar = RandomVariate.of(distribution);
      Tensor point = RandomVariate.of(distribution, 2);
      Tensor fwd = bijectionFamily.inverse(scalar).apply(point);
      assertTrue(Chop._12.close(bijectionFamily.forward(scalar).apply(fwd), point));
    }
  }

  public void testForwardSe2() {
    RigidFamily rigidFamily = new So2Family(s -> s);
    Tensor matrix = rigidFamily.forward_se2(RealScalar.ONE);
    assertTrue(OrthogonalMatrixQ.of(matrix, Chop._14));
    assertEquals(matrix, Se2Utils.toSE2Matrix(Tensors.vector(0, 0, 1)));
  }
}
