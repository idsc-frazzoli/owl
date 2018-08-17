// code by jph
package ch.ethz.idsc.owl.math.dubins;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ interface DubinsSteer {
  Optional<Tensor> steer(double dist_tr, double th_tr, double th_total, Scalar radius);
}
