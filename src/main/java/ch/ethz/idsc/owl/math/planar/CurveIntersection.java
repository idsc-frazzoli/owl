// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;

public interface CurveIntersection {
  /** @param tensor of points on non-cyclic trail ahead
   * @param distance look ahead
   * @return point interpolated on trail at given distance */
  Optional<Tensor> string(Tensor tensor);

  Optional<Tensor> cyclic(Tensor tensor);
}
