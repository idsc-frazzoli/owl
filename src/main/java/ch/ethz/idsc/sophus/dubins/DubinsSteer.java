// code by jph
package ch.ethz.idsc.sophus.dubins;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
/* package */ interface DubinsSteer {
  /** @param dist_tr non-negative
   * @param th_tr in the interval [0, 2*pi)
   * @param th_total in the interval [0, 2*pi)
   * @param radius positive
   * @return vector with 3 entries as length of dubins path segments */
  Optional<Tensor> steer(Scalar dist_tr, Scalar th_tr, Scalar th_total, Scalar radius);
}
