// code by jph
package ch.ethz.idsc.sophus.app.curve;

import ch.ethz.idsc.sophus.curve.GeodesicBSplineInterpolation;
import ch.ethz.idsc.sophus.curve.GeodesicBSplineInterpolation.Iteration;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum BSplineInterpolationSequence {
  ;
  public static Tensor of(GeodesicBSplineInterpolation geodesicBSplineInterpolation) {
    Tensor tensor = Tensors.empty();
    Iteration iteration = geodesicBSplineInterpolation.init();
    for (int count = 0; count < 10; ++count) {
      iteration = iteration.step();
      tensor.append(iteration.control());
    }
    return tensor;
  }
}
