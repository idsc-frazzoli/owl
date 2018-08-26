// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clip;

public class CircleCurveIntersection implements CurveIntersection {
  private final Scalar distance;

  public CircleCurveIntersection(Scalar radius) {
    this.distance = radius;
  }

  @Override
  public Optional<Tensor> string(Tensor tensor) {
    return universal(tensor, tensor.length());
  }

  @Override
  public Optional<Tensor> cyclic(Tensor tensor) {
    return universal(tensor, tensor.length() + 1);
  }

  private Optional<Tensor> universal(Tensor tensor, int max) {
    if (1 < tensor.length()) { // tensor is required to contain at least two entries
      Tensor prev = tensor.get(0);
      Scalar lo = Norm._2.of(prev);
      for (int count = 1; count < max; ++count) {
        Tensor next = tensor.get(count % tensor.length());
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
}
