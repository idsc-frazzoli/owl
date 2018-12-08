// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.group.RnGeodesic;
import ch.ethz.idsc.owl.math.group.RnGroup;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BSplineInterpolationApproximationTest extends TestCase {
  public void testRn() {
    int degree = 2;
    BSplineInterpolationApproximation bSplineInterpolationApproximation = //
        new BSplineInterpolationApproximation(RnGroup.INSTANCE, RnGeodesic.INSTANCE, degree);
    Tensor target = Tensors.vector(1, 2, 0, 2, 1, 3);
    Tensor control = bSplineInterpolationApproximation.fixed(target, 20);
    Tensor domain = Range.of(0, target.length());
    Tensor result = domain.map(bSplineInterpolationApproximation.geodesicBSplineFunction(control));
    Scalar scalar = Norm._2.ofVector(result.subtract(target));
    assertTrue(Chop._05.allZero(scalar));
  }
}
