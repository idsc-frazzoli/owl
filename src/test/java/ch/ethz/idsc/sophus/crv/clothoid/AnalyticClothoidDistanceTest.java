// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class AnalyticClothoidDistanceTest extends TestCase {
  public void testEuclidean() {
    Tensor p = Tensors.vector(1, 2, 0);
    Tensor q = Tensors.vector(9, 2, 0);
    Scalar scalar = AnalyticClothoidDistance.LR1.distance(p, q);
    assertEquals(scalar, RealScalar.of(8));
    ExactScalarQ.require(scalar);
  }

  public void testOrigin() {
    assertEquals(AnalyticClothoidDistance.LR1.norm(Tensors.vector(0, 0, 0)), RealScalar.of(0));
    assertEquals(AnalyticClothoidDistance.LR3.norm(Tensors.vector(0, 0, 0)), RealScalar.of(0));
  }

  public void testSome() {
    Tensor pose = Tensors.vector(2, 1, -0.3);
    Scalar lr1 = AnalyticClothoidDistance.LR1.norm(pose);
    Scalar lr3 = AnalyticClothoidDistance.LR3.norm(pose);
    Chop._12.requireClose(lr1, lr3);
  }

  public void testNonNegative() {
    for (int index = 0; index < 100; ++index) {
      Tensor pose = RandomVariate.of(NormalDistribution.standard(), 3);
      Scalar lr1 = AnalyticClothoidDistance.LR1.norm(pose);
      Scalar lr3 = AnalyticClothoidDistance.LR3.norm(pose);
      Chop._12.requireClose(lr1, lr3);
      Sign.requirePositive(lr1);
    }
  }

  public void testRandomDifference() {
    Chop chop = Chop.below(1);
    for (int index = 0; index < 100; ++index) {
      Distribution distribution = NormalDistribution.standard();
      Tensor p = RandomVariate.of(distribution, 3);
      Tensor q = RandomVariate.of(distribution, 3);
      Scalar lr1 = AnalyticClothoidDistance.LR1.distance(p, q);
      Scalar lr3 = AnalyticClothoidDistance.LR3.distance(p, q);
      Scalar pcd = PseudoClothoidDistance.INSTANCE.distance(p, q);
      Sign.requirePositive(lr1);
      Sign.requirePositive(lr3);
      Sign.requirePositive(pcd);
      Scalar max = Norm.INFINITY.ofVector(Tensors.of(lr1, lr3, pcd));
      Scalar di1 = lr1.subtract(pcd).abs().divide(max);
      Scalar dip = lr1.subtract(lr3).abs().divide(max);
      Scalar di3 = lr3.subtract(pcd).abs().divide(max);
      chop.requireClose(di1, RealScalar.ZERO);
      chop.requireClose(dip, RealScalar.ZERO);
      chop.requireClose(di3, RealScalar.ZERO);
    }
  }

  public void testSameAngleNonStraight() {
    Chop chop = Chop.below(1);
    for (int index = 0; index < 100; ++index) {
      Distribution distribution = NormalDistribution.standard();
      Tensor p = RandomVariate.of(distribution, 3);
      Tensor q = RandomVariate.of(distribution, 3);
      q.set(p.get(2), 2);
      Scalar lr1 = AnalyticClothoidDistance.LR1.distance(p, q);
      Scalar lr3 = AnalyticClothoidDistance.LR3.distance(p, q);
      Scalar pcd = PseudoClothoidDistance.INSTANCE.distance(p, q);
      Sign.requirePositive(lr1);
      Sign.requirePositive(lr3);
      Sign.requirePositive(pcd);
      Scalar max = Norm.INFINITY.ofVector(Tensors.of(lr1, lr3, pcd));
      Scalar di1 = lr1.subtract(pcd).abs().divide(max);
      Scalar dip = lr1.subtract(lr3).abs().divide(max);
      Scalar di3 = lr3.subtract(pcd).abs().divide(max);
      chop.requireClose(di1, RealScalar.ZERO);
      chop.requireClose(dip, RealScalar.ZERO);
      chop.requireClose(di3, RealScalar.ZERO);
    }
  }
}
