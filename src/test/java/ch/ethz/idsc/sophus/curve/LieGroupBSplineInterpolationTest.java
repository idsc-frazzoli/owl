// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.curve.LieGroupBSplineInterpolation.Iteration;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.RnGroup;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.pdf.BernoulliDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

public class LieGroupBSplineInterpolationTest extends TestCase {
  public void testRn() {
    int degree = 2;
    LieGroupBSplineInterpolation lieGroupBSplineInterpolation = //
        new LieGroupBSplineInterpolation(RnGroup.INSTANCE, RnGeodesic.INSTANCE, degree);
    Tensor target = Tensors.vector(1, 2, 0, 2, 1, 3);
    Tensor control = lieGroupBSplineInterpolation.apply(target);
    Tensor domain = Range.of(0, target.length());
    Tensor result = domain.map(lieGroupBSplineInterpolation.geodesicBSplineFunction(control));
    Scalar scalar = Norm._2.ofVector(result.subtract(target));
    assertTrue(Chop._05.allZero(scalar));
  }

  public void testIterationRnExact() {
    int degree = 2;
    LieGroupBSplineInterpolation lieGroupBSplineInterpolation = //
        new LieGroupBSplineInterpolation(RnGroup.INSTANCE, RnGeodesic.INSTANCE, degree);
    Tensor target = Tensors.vector(1, 2, 0, 2, 1, 3);
    Iteration it0 = lieGroupBSplineInterpolation.start(target);
    Iteration it1 = it0.step();
    assertTrue(ExactScalarQ.all(it1.control()));
  }

  public void testApplyRn() {
    int degree = 2;
    LieGroupBSplineInterpolation lieGroupBSplineInterpolation = //
        new LieGroupBSplineInterpolation(RnGroup.INSTANCE, RnGeodesic.INSTANCE, degree);
    Tensor target = N.DOUBLE.of(Tensors.vector(1, 2, 0, 2, 1, 3));
    Tensor control = lieGroupBSplineInterpolation.apply(target);
    Tensor vector = Tensors.vector(1, 2.7510513036161504, -0.922624053826282, 2.784693019343523, 0.21446593776315992, 3);
    assertTrue(Chop._10.close(control, vector));
  }

  public void testApplyRnExact() {
    int degree = 2;
    LieGroupBSplineInterpolation lieGroupBSplineInterpolation = //
        new LieGroupBSplineInterpolation(RnGroup.INSTANCE, RnGeodesic.INSTANCE, degree);
    Tensor target = Tensors.vector(1, 2, 0, 2, 1, 3);
    Tensor control = lieGroupBSplineInterpolation.apply(target);
    Tensor vector = Tensors.vector(1, 2.7510513036161504, -0.922624053826282, 2.784693019343523, 0.21446593776315992, 3);
    assertTrue(Chop._10.close(control, vector));
  }

  public void testExactRnConvergence() {
    int degree = 3;
    LieGroupBSplineInterpolation lieGroupBSplineInterpolation = //
        new LieGroupBSplineInterpolation(RnGroup.INSTANCE, RnGeodesic.INSTANCE, degree);
    Tensor target = RandomVariate.of(BernoulliDistribution.of(RationalScalar.HALF), 10, 3);
    lieGroupBSplineInterpolation.apply(target);
  }

  public void testIterationRnConvergence() {
    int degree = 3;
    LieGroupBSplineInterpolation lieGroupBSplineInterpolation = //
        new LieGroupBSplineInterpolation(RnGroup.INSTANCE, RnGeodesic.INSTANCE, degree);
    Tensor target = N.DOUBLE.of(Tensors.vector(1, 2, 0, 2, 1, 3));
    Iteration iteration = lieGroupBSplineInterpolation.start(target);
    Tensor p = iteration.control();
    for (int index = 0; index < 100; ++index) {
      iteration = iteration.step();
      Tensor q = iteration.control();
      if (Scalars.isZero(Norm._2.between(p, q)))
        return;
      p = q;
    }
    fail();
  }
}
