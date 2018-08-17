// code by jph
package ch.ethz.idsc.owl.math.dubins;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ interface DubinsSteer {
  /** @param dist_tr non-negative
   * @param th_tr in the interval [0, 2*pi)
   * @param th_total in the interval [0, 2*pi)
   * @param radius positive
   * @return vector with 3 entries as length of dubins path segments */
  Optional<Tensor> steer(double dist_tr, double th_tr, double th_total, Scalar radius);
}
