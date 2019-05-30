// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** evaluates distance of points in given curve to origin
 * if two successive points have a distance that crosses radius
 * the point that is the result of (linear-)interpolation is the
 * result of the intersection. */
public abstract class SimpleCurveIntersection implements CurveIntersection, Serializable {
  protected final Scalar radius;

  protected SimpleCurveIntersection(Scalar radius) {
    this.radius = radius;
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
          return Optional.of(new CurvePoint((count - 1) % tensor_length, split(prev, next, clip.rescale(radius))));
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

  /** @param prev
   * @param next
   * @param scalar
   * @return */
  protected abstract Tensor split(Tensor prev, Tensor next, Scalar scalar);
}
