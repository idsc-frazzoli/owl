// code by jph
package ch.ethz.idsc.owl.math.pursuit;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.itp.BinaryAverage;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

/** evaluates distance of points in given curve to origin
 * if two successive points have a distance that crosses radius
 * the point that is the result of (linear-)interpolation is the
 * result of the intersection. */
public abstract class SimpleCurveIntersection implements CurveIntersection, Serializable {
  private final Scalar radius;
  private final BinaryAverage binaryAverage;

  /** @param radius non-negative
   * @param binaryAverage non-null
   * @throws Exception if given radius is negative */
  protected SimpleCurveIntersection(Scalar radius, BinaryAverage binaryAverage) {
    this.radius = Sign.requirePositiveOrZero(radius);
    this.binaryAverage = Objects.requireNonNull(binaryAverage);
  }

  @Override // from CurveIntersection
  public final Optional<Tensor> cyclic(Tensor tensor) {
    return universal(tensor, 0).map(CurvePoint::getTensor);
  }

  @Override // from CurveIntersection
  public final Optional<Tensor> string(Tensor tensor) {
    return universal(tensor, 1).map(CurvePoint::getTensor);
  }

  /** @param tensor
   * @param first typically 0 or 1
   * @return */
  protected final Optional<CurvePoint> universal(Tensor tensor, int first) {
    int tensor_length = tensor.length();
    if (1 < tensor_length) { // tensor is required to contain at least two entries
      Tensor prev = tensor.get((first + tensor_length - 1) % tensor_length);
      Scalar lo = distance(prev);
      for (int count = first; count < tensor_length; ++count) {
        Tensor next = tensor.get(count);
        Scalar hi = distance(next); // "hi" may even be less than "lo"
        if (Scalars.lessEquals(lo, radius) && Scalars.lessEquals(radius, hi)) {
          Clip clip = Clips.interval(lo, hi); // lo <= distance <= hi
          Tensor split = binaryAverage.split(prev, next, clip.rescale(radius));
          return Optional.of(new CurvePoint((count - 1) % tensor_length, split));
        }
        prev = next;
        lo = hi;
      }
    }
    return Optional.empty();
  }

  /** @param tensor
   * @return distance to given tensor */
  protected abstract Scalar distance(Tensor tensor);
}
