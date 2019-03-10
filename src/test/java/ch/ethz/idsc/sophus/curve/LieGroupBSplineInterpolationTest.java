// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.curve.AbstractBSplineInterpolation.Iteration;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.RnGroup;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.BernoulliDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

public class LieGroupBSplineInterpolationTest extends TestCase {
  public void testIterationRnExact() {
    Tensor target = Tensors.vector(1, 2, 0, 2, 1, 3);
    LieGroupBSplineInterpolation lieGroupBSplineInterpolation = //
        new LieGroupBSplineInterpolation(RnGroup.INSTANCE, RnGeodesic.INSTANCE, 2, target);
    Iteration it0 = lieGroupBSplineInterpolation.init();
    Iteration it1 = it0.stepGaussSeidel();
    ExactTensorQ.require(it1.control());
  }

  public void testApplyRn() {
    Tensor target = N.DOUBLE.of(Tensors.vector(1, 2, 0, 2, 1, 3));
    LieGroupBSplineInterpolation lieGroupBSplineInterpolation = //
        new LieGroupBSplineInterpolation(RnGroup.INSTANCE, RnGeodesic.INSTANCE, 2, target);
    Tensor control = lieGroupBSplineInterpolation.apply();
    Tensor vector = Tensors.vector(1, 2.7510513036161504, -0.922624053826282, 2.784693019343523, 0.21446593776315992, 3);
    assertTrue(Chop._10.close(control, vector));
  }

  public void testApplyRnExact() {
    Tensor target = Tensors.vector(1, 2, 0, 2, 1, 3);
    LieGroupBSplineInterpolation lieGroupBSplineInterpolation = //
        new LieGroupBSplineInterpolation(RnGroup.INSTANCE, RnGeodesic.INSTANCE, 2, target);
    Tensor control = lieGroupBSplineInterpolation.apply();
    Tensor vector = Tensors.vector(1, 2.7510513036161504, -0.922624053826282, 2.784693019343523, 0.21446593776315992, 3);
    assertTrue(Chop._10.close(control, vector));
  }

  public void testExactRnConvergence() {
    Tensor target = RandomVariate.of(BernoulliDistribution.of(RationalScalar.HALF), 10, 3);
    LieGroupBSplineInterpolation lieGroupBSplineInterpolation = //
        new LieGroupBSplineInterpolation(RnGroup.INSTANCE, RnGeodesic.INSTANCE, 3, target);
    lieGroupBSplineInterpolation.apply();
  }

  public void testIterationRnConvergence() {
    Tensor target = N.DOUBLE.of(Tensors.vector(1, 2, 0, 2, 1, 3));
    LieGroupBSplineInterpolation lieGroupBSplineInterpolation = //
        new LieGroupBSplineInterpolation(RnGroup.INSTANCE, RnGeodesic.INSTANCE, 3, target);
    Iteration iteration = lieGroupBSplineInterpolation.init();
    Tensor p = iteration.control();
    for (int index = 0; index < 100; ++index) {
      iteration = iteration.stepGaussSeidel();
      Tensor q = iteration.control();
      if (Scalars.isZero(Norm._2.between(p, q)))
        return;
      p = q;
    }
    fail();
  }
}
