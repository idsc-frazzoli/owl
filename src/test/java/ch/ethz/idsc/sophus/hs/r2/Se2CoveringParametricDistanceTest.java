// code by jph, ob 
package ch.ethz.idsc.sophus.hs.r2;

import ch.ethz.idsc.sophus.lie.se2.Se2ParametricDistance;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CoveringParametricDistanceTest extends TestCase {
  public void testSimple() {
    Scalar scalar = Se2CoveringParametricDistance.INSTANCE.distance(Tensors.vector(1, 2, 3), Tensors.vector(1 + 3, 2 + 4, 3));
    assertEquals(scalar, RealScalar.of(5));
  }

  public void testInfinity() {
    Scalar scalar = Se2CoveringParametricDistance.INSTANCE.distance(Tensors.vector(1, 2, 3), Tensors.vector(1 + 3, 2 + 4, 3 + Math.PI * 2));
    assertTrue(Scalars.lessThan(RealScalar.of(1e10), scalar));
  }

  public void testHalf() {
    Scalar scalar = Se2CoveringParametricDistance.INSTANCE.distance(Tensors.vector(1, 2, 3), Tensors.vector(1, 2 + 2 * 3, 3 + Math.PI));
    Chop._14.requireClose(scalar, RealScalar.of(3 * Math.PI));
  }

  public void testSE2() {
    double rand = Math.random();
    Scalar scalarSE2C = Se2CoveringParametricDistance.INSTANCE.distance(Tensors.vector(1, 2, 0), Tensors.vector(1, 2 + 2 * 3, rand * Math.PI));
    Scalar scalarSE2 = Se2ParametricDistance.INSTANCE.distance(Tensors.vector(1, 2, 0), Tensors.vector(1, 2 + 2 * 3, rand * Math.PI));
    Chop._14.requireClose(scalarSE2, scalarSE2C);
  }

  public void testSimpleUnits() {
    Scalar scalar = Se2CoveringParametricDistance.INSTANCE.distance(Tensors.fromString("{1[m], 2[m], 3}"), Tensors.fromString("{4[m], 6[m], 3}"));
    assertEquals(scalar, Quantity.of(5, "m"));
  }

  public void testOtherUnits() {
    Scalar scalar = Se2CoveringParametricDistance.INSTANCE.distance(Tensors.fromString("{1[m], 2[m], 3}"), Tensors.fromString("{4[m], 6[m], 3.3}"));
    Chop._12.close(scalar, Quantity.of(5.018799335788676, "m"));
  }

  public void testUnitCircle54Pi() {
    Tensor p = Array.zeros(3);
    Tensor q = Tensors.vector(1, 1, Math.PI / 2 + 2 * Math.PI);
    Scalar se2 = Se2ParametricDistance.INSTANCE.distance(p, q);
    Chop._14.requireClose(se2, Pi.HALF);
    Scalar se2c = Se2CoveringParametricDistance.INSTANCE.distance(p, q);
    Chop._14.requireClose(se2c, Pi.HALF.add(Pi.TWO));
  }

  public void testUnitCircle34Pi() {
    Tensor p = Array.zeros(3);
    Tensor q = Tensors.vector(1, 1, Math.PI / 2 - 2 * Math.PI);
    Scalar se2 = Se2ParametricDistance.INSTANCE.distance(p, q);
    Chop._14.requireClose(se2, Pi.HALF);
    Scalar se2c = Se2CoveringParametricDistance.INSTANCE.distance(p, q);
    Chop._14.requireClose(se2c, Pi.TWO.multiply(RationalScalar.of(3, 4)));
  }

  public void testUnitCircle34Pib() {
    Tensor p = Array.zeros(3);
    Tensor q = Tensors.vector(1, 1, Math.PI / 2 - 4 * Math.PI);
    Scalar se2 = Se2ParametricDistance.INSTANCE.distance(p, q);
    Chop._14.requireClose(se2, Pi.HALF);
    Scalar se2c = Se2CoveringParametricDistance.INSTANCE.distance(p, q);
    Chop._14.requireClose(se2c, Pi.TWO.multiply(RationalScalar.of(7, 4)));
  }

  public void testOrderInvariant() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor p = RandomVariate.of(distribution, 3);
      Tensor q = RandomVariate.of(distribution, 3);
      Scalar dpq = Se2CoveringParametricDistance.INSTANCE.distance(p, q);
      Scalar dqp = Se2CoveringParametricDistance.INSTANCE.distance(q, p);
      Chop._14.requireClose(dpq, dqp);
    }
  }
}
