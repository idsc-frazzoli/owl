// code by jph
package ch.ethz.idsc.sophus.app.curve;

import ch.ethz.idsc.sophus.crv.spline.AbstractBSplineInterpolation;
import ch.ethz.idsc.sophus.crv.spline.AbstractBSplineInterpolation.Iteration;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum BSplineInterpolationSequence {
  ;
  public static Tensor of(AbstractBSplineInterpolation abstractBSplineInterpolation) {
    int steps = 10;
    Tensor tensor = Tensors.reserve(steps);
    Iteration iteration = abstractBSplineInterpolation.init();
    for (int count = 0; count < steps; ++count) {
      iteration = iteration.stepGaussSeidel();
      tensor.append(iteration.control());
    }
    return tensor;
  }
}
