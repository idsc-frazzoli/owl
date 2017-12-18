// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clip;

public enum PurePursuitUtil {
  ;
  /** @param tensor of points on trail ahead
   * @param distance look ahead
   * @return beacon location on segment of trails that is the result of linear interpolation with
   * approximately given distance from origin */
  public static Optional<Tensor> beacon(Tensor tensor, Scalar distance) {
    // TODO implementation can be made more efficient
    for (int count = 1; count < tensor.length(); ++count) {
      Tensor prev = tensor.get(count - 1);
      Tensor next = tensor.get(count - 0);
      Scalar lo = Norm._2.of(prev);
      Scalar hi = Norm._2.of(next);
      if (Scalars.lessEquals(lo, distance) && Scalars.lessEquals(distance, hi)) {
        Clip clip = Clip.function(lo, hi);
        Scalar lambda = clip.rescale(distance);
        Interpolation interpolation = LinearInterpolation.of(Tensors.of(prev, next));
        return Optional.of(interpolation.get(Tensors.of(lambda)));
      }
    }
    return Optional.empty();
  }
}
