// code by jph
package ch.ethz.idsc.sophus.app.curve;

import ch.ethz.idsc.sophus.curve.AbstractBSplineInterpolation;
import ch.ethz.idsc.sophus.curve.AbstractBSplineInterpolation.Iteration;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum BSplineInterpolationSequence {
  ;
  public static Tensor of(AbstractBSplineInterpolation geodesicBSplineInterpolation) {
    Tensor tensor = Tensors.empty();
    Iteration iteration = geodesicBSplineInterpolation.init();
    for (int count = 0; count < 10; ++count) {
      iteration = iteration.stepGaussSeidel();
      tensor.append(iteration.control());
    }
    return tensor;
  }
}
