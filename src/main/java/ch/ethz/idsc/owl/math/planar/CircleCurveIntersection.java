// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** intersection with circle centered at (0, 0) and given radius.
 * 
 * input to intersection query is either a non-cyclic or cyclic polygon
 * the output is the coordinate of intersection using linear interpolation. */
public class CircleCurveIntersection implements CurveIntersection, Serializable {
  private final Scalar distance;

  public CircleCurveIntersection(Scalar radius) {
    this.distance = radius;
  }

  @Override // from CurveIntersection
  public Optional<Tensor> cyclic(Tensor tensor) {
    return universal(tensor, 0);
  }

  @Override // from CurveIntersection
  public Optional<Tensor> string(Tensor tensor) {
    return universal(tensor, 1);
  }

  private Optional<Tensor> universal(Tensor tensor, final int first) {
    int tensor_length = tensor.length();
    if (1 < tensor_length) { // tensor is required to contain at least two entries
      Tensor prev = tensor.get((first + tensor_length - 1) % tensor_length);
      Scalar lo = Norm._2.of(prev);
      for (int count = first; count < tensor_length; ++count) {
        Tensor next = tensor.get(count);
        Scalar hi = Norm._2.of(next); // "hi" may even be less than "lo"
        if (Scalars.lessEquals(lo, distance) && Scalars.lessEquals(distance, hi)) {
          Clip clip = Clips.interval(lo, hi); // lo <= distance <= hi
          Interpolation interpolation = LinearInterpolation.of(Tensors.of(prev, next));
          Scalar lambda = clip.rescale(distance);
          return Optional.of(interpolation.at(lambda));
        }
        prev = next;
        lo = hi;
      }
    }
    return Optional.empty();
  }
}
