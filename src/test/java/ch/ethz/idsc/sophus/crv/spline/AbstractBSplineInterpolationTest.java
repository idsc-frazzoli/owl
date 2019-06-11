// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.crv.spline.AbstractBSplineInterpolation.Iteration;
import ch.ethz.idsc.sophus.hs.sn.SnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class AbstractBSplineInterpolationTest extends TestCase {
  public void testS2() {
    Tensor target = Tensors.fromString("{{1,0,0},{0,1,0},{0,0,1},{-1,0,0}}");
    AbstractBSplineInterpolation geodesicBSplineInterpolation = //
        new GeodesicBSplineInterpolation(SnGeodesic.INSTANCE, 2, target);
    Iteration iteration = geodesicBSplineInterpolation.untilClose(Chop._08, 100);
    assertTrue(iteration.steps() < 100);
    Tensor control = iteration.control();
    Chop._12.requireClose(control.get(0), target.get(0));
    Chop._12.requireClose(control.get(3), target.get(3));
    GeodesicBSplineFunction geodesicBSplineFunction = //
        GeodesicBSplineFunction.of(SnGeodesic.INSTANCE, 2, control);
    Chop._06.requireClose(target, Range.of(0, 4).map(geodesicBSplineFunction));
  }

  public void testS2Jacobi() {
    Tensor target = Tensors.fromString("{{1,0,0},{0,1,0},{0,0,1},{-1,0,0}}");
    AbstractBSplineInterpolation geodesicBSplineInterpolation = //
        new GeodesicBSplineInterpolation(SnGeodesic.INSTANCE, 2, target);
    Iteration iteration = geodesicBSplineInterpolation.init();
    assertTrue(iteration.steps() < 100);
    for (int c = 0; c < 20; ++c)
      iteration = iteration.stepJacobi();
    Tensor control = iteration.control();
    Chop._12.requireClose(control.get(0), target.get(0));
    Chop._12.requireClose(control.get(3), target.get(3));
    GeodesicBSplineFunction geodesicBSplineFunction = //
        GeodesicBSplineFunction.of(SnGeodesic.INSTANCE, 2, control);
    Chop._06.requireClose(target, Range.of(0, 4).map(geodesicBSplineFunction));
  }
}
