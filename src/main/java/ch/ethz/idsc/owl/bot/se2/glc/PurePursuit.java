// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
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

public enum PurePursuit {
  ;
  private static final Scalar TWO = RealScalar.of(2);

  /** @param tensor of points on trail ahead
   * @param distance look ahead
   * @return beacon location on segment of trails that is the result
   * of linear interpolation at given distance from origin */
  public static Optional<Tensor> beacon(Tensor tensor, Scalar distance) {
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
          return Optional.of(interpolation.get(Tensors.of(lambda)));
        }
        prev = next;
        lo = hi;
      }
    }
    return Optional.empty();
  }

  /** @param tensor
   * @param distance
   * @return rate with interpretation rad*m^-1, or empty if the first coordinate
   * of the look ahead beacon is non-positive
   * @see CarFlows */
  public static Optional<Scalar> turningRatePositiveX(Tensor tensor, Scalar distance) {
    Optional<Tensor> optional = beacon(tensor, distance);
    if (optional.isPresent()) { //
      Tensor lookAhead = optional.get(); // {x, y}
      Scalar x = lookAhead.Get(0);
      if (Sign.isPositive(x)) {
        Scalar angle = ArcTan.of(x, lookAhead.Get(1));
        // in the formula below, 2 is not a magic constant
        // but has an exact geometric interpretation
        return Optional.of(Sin.FUNCTION.apply(angle.multiply(TWO)).divide(x));
      }
    }
    return Optional.empty();
  }
}
