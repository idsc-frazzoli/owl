// code by jph
package ch.ethz.idsc.owl.math;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** functionality used in {@code TrajectoryPlanner} to map state coordinates to
 * the coordinates that imply domain keys */
public interface CoordinateWrap extends Serializable {
  /** @param x
   * @return coordinate transform of x before obtaining domain key */
  Tensor represent(Tensor x);

  /** @param p
   * @param q
   * @return distance between p and q */
  Scalar distance(Tensor p, Tensor q);
}
