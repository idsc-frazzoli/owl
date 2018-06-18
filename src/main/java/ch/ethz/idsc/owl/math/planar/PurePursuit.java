// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sin;

// TODO class contains 2 formulas in one: fining beacon, and computing turning rate to selected target lookAhead
// ... better to split!
public class PurePursuit {
  private static final Scalar TWO = DoubleScalar.of(2);

  /** @param tensor of waypoints {{x1, y1}, {x2, y2}, ...}
   * @param distance to look ahead
   * @return */
  public static PurePursuit fromTrajectory(Tensor tensor, Scalar distance) {
    return new PurePursuit(tensor, distance);
  }
  // ---

  private final Optional<Tensor> lookAhead;
  private final Optional<Scalar> ratio;

  private PurePursuit(Tensor tensor, Scalar distance) {
    lookAhead = beacon(tensor, distance);
    ratio = lookAhead.isPresent() ? ratioPositiveX(lookAhead.get()) : Optional.empty();
  }

  public Optional<Tensor> lookAhead() {
    return lookAhead;
  }

  public Optional<Scalar> ratio() {
    return ratio;
  }

  /** @param tensor of points on trail ahead
   * @param distance look ahead
   * @return point interpolated on trail at given distance */
  private static Optional<Tensor> beacon(Tensor tensor, Scalar distance) {
    if (1 < tensor.length()) { // tensor is required to contain at least two entries
      Tensor prev = tensor.get(0);
      Scalar lo = Norm._2.of(prev);
      for (int count = 1; count < tensor.length(); ++count) {
        Tensor next = tensor.get(count - 0);
        Scalar hi = Norm._2.of(next);
        if (Scalars.lessEquals(lo, distance) && Scalars.lessEquals(distance, hi)) {
          Clip clip = Clip.function(lo, hi); // lo <= distance <= hi
          Scalar lambda = clip.rescale(distance);
          Interpolation interpolation = LinearInterpolation.of(Tensors.of(prev, next));
          return Optional.of(interpolation.at(lambda));
        }
        prev = next;
        lo = hi;
      }
    }
    return Optional.empty();
  }

  /** @param lookAhead {x, y, ...} where x is positive
   * @return rate with interpretation rad*m^-1, or empty if the first coordinate
   * of the look ahead beacon is non-positive
   * @throws Exception if lookAhead has insufficient length */
  public static Optional<Scalar> ratioPositiveX(Tensor lookAhead) {
    Scalar x = lookAhead.Get(0);
    if (Sign.isPositive(x)) {
      Scalar angle = ArcTan.of(x, lookAhead.Get(1));
      // in the formula below, 2 is not a magic constant
      // but has an exact geometric interpretation
      return Optional.of(Sin.FUNCTION.apply(angle.multiply(TWO)).divide(x));
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
}
