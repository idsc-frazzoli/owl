// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sin;

public class PurePursuit {
  /** @param lookAhead {x, y, ...} where x is positive
   * @return rate with interpretation rad*m^-1, or empty if the first coordinate
   * of the look ahead beacon is non-positive
   * @throws Exception if lookAhead has insufficient length */
  public static Optional<Scalar> ratioPositiveX(Tensor lookAhead) {
    Scalar x = lookAhead.Get(0);
    if (Sign.isPositive(x)) {
      Scalar angle = ArcTan.of(x, lookAhead.Get(1));
      // in the formula below, 2*angle == angle+angle is not a magic constant
      // but has an exact geometric interpretation
      return Optional.of(Sin.FUNCTION.apply(angle.add(angle)).divide(x));
    }
    return Optional.empty();
  }

  /** @param lookAhead {x, y, ...} where x is negative
   * @return rate with interpretation rad*m^-1, or empty if the first coordinate
   * of the look ahead beacon is non-positive
   * @throws Exception if lookAhead has insufficient length */
  public static Optional<Scalar> ratioNegativeX(Tensor lookAhead) {
    Tensor target = lookAhead.copy();
    target.set(Scalar::negate, 0);
    return ratioPositiveX(target);
  }

  /** @param tensor of waypoints {{x1, y1}, {x2, y2}, ...}
   * @param distance to look ahead
   * @return */
  public static PurePursuit fromTrajectory(Tensor tensor, Scalar distance) {
    return new PurePursuit(new CircleCurveIntersection(distance).string(tensor));
  }

  // ---
  private final Optional<Tensor> lookAhead;
  private final Optional<Scalar> ratio;

  public PurePursuit(Optional<Tensor> lookAhead) {
    this.lookAhead = lookAhead;
    ratio = lookAhead.isPresent() //
        ? ratioPositiveX(lookAhead.get())
        : Optional.empty();
  }

  public Optional<Tensor> lookAhead() {
    return lookAhead;
  }

  /** @return */
  public Optional<Scalar> ratio() {
    return ratio;
  }
}
