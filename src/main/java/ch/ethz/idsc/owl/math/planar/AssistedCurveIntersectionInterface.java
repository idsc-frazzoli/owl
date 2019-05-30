// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;

public interface AssistedCurveIntersectionInterface {
  /** @param tensor of points on cyclic trail ahead
   * @param prevIdx search seed
   * @return point interpolated on trail at given distance */
  Optional<CurvePoint> cyclic(Tensor tensor, int prevIdx);

  /** @param tensor of points on non-cyclic trail ahead
   * @param prevIdx search seed
   * @return point interpolated on trail at given distance */
  Optional<CurvePoint> string(Tensor tensor, int prevIdx);
}
