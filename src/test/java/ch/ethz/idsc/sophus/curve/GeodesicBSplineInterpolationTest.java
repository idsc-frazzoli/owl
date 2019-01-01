// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.curve.GeodesicBSplineInterpolation.Iteration;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.space.H2Geodesic;
import ch.ethz.idsc.sophus.space.SnGeodesic;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

public class GeodesicBSplineInterpolationTest extends TestCase {
  public void testApplyRn() {
    Tensor target = N.DOUBLE.of(Tensors.vector(1, 2, 0, 2, 1, 3));
    GeodesicBSplineInterpolation geodesicBSplineInterpolation = //
        new GeodesicBSplineInterpolation(RnGeodesic.INSTANCE, 2, target);
    Tensor control = geodesicBSplineInterpolation.apply();
    Tensor vector = Tensors.vector(1, 2.7510513036161504, -0.922624053826282, 2.784693019343523, 0.21446593776315992, 3);
    assertTrue(Chop._10.close(control, vector));
  }

  public void testMoveRn() {
    Tensor tensor = RandomVariate.of(DiscreteUniformDistribution.of(2, 100), 3, 5);
    GeodesicBSplineInterpolation geodesicBSplineInterpolation = //
        new GeodesicBSplineInterpolation(RnGeodesic.INSTANCE, 2, tensor);
    Tensor prev = tensor.get(0);
    Tensor eval = tensor.get(1);
    Tensor goal = tensor.get(2);
    Tensor pos0 = geodesicBSplineInterpolation.move(prev, eval, goal);
    Tensor pos1 = geodesicBSplineInterpolation.test(prev, eval, goal);
    assertEquals(pos0, pos1);
    assertTrue(ExactScalarQ.all(pos0));
    assertTrue(ExactScalarQ.all(pos1));
    Iteration iteration = geodesicBSplineInterpolation.init();
    assertEquals(iteration.steps(), 0);
  }

  public void testH2() {
    Tensor target = Tensors.fromString("{{0,2},{1,2},{2,2}}");
    GeodesicBSplineInterpolation geodesicBSplineInterpolation = //
        new GeodesicBSplineInterpolation(H2Geodesic.INSTANCE, 3, target);
    target = N.DOUBLE.of(target);
    Iteration iteration = geodesicBSplineInterpolation.untilClose(Chop._08, 100);
    assertTrue(iteration.steps() < 100);
  }

  public void testS2() {
    Tensor target = Tensors.fromString("{{1,0,0},{0,1,0},{0,0,1},{-1,0,0}}");
    GeodesicBSplineInterpolation geodesicBSplineInterpolation = //
        new GeodesicBSplineInterpolation(SnGeodesic.INSTANCE, 2, target);
    Iteration iteration = geodesicBSplineInterpolation.untilClose(Chop._08, 100);
    assertTrue(iteration.steps() < 100);
    Tensor control = iteration.control();
    Chop._12.requireClose(control.get(0), target.get(0));
    Chop._12.requireClose(control.get(3), target.get(3));
  }
}
