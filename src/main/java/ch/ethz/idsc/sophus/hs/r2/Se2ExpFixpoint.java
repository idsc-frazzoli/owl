// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.sca.Chop;

public enum Se2ExpFixpoint {
  ;
  /** @param velocity {vx[m*s^-1], vy[m*s^-1], omega[s^-1]}
   * @return point {px[m], py[m]} in the plane that is fixed by the group action exp(t velocity) for any t */
  public static Optional<Tensor> of(Tensor velocity) {
    Scalar omega = velocity.Get(2);
    return Scalars.isZero(omega) //
        ? Optional.empty()
        : Optional.of(Cross.of(velocity.extract(0, 2)).divide(omega));
  }

  public static Optional<Tensor> of(Tensor velocity, Chop chop) {
    Scalar omega = velocity.Get(2);
    return chop.allZero(omega) //
        ? Optional.empty()
        : Optional.of(Cross.of(velocity.extract(0, 2)).divide(omega)); // TODO JPH test coverage
  }
}
