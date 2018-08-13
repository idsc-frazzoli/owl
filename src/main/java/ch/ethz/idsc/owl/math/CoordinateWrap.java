// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Tensor;

/** functionality used in {@code TrajectoryPlanner} to map state coordinates to
 * the coordinates that imply domain keys */
public interface CoordinateWrap extends TensorDifference {
  /** @param x
   * @return coordinate transform of x before obtaining domain key */
  Tensor represent(Tensor x);
}
