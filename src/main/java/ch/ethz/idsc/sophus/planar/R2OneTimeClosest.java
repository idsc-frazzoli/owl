// code by jph
package ch.ethz.idsc.sophus.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

public class R2OneTimeClosest {
  private final Scalar threshold;

  public R2OneTimeClosest(Scalar threshold) {
    this.threshold = threshold;
  }

  public Optional<Integer> index(Tensor control, Tensor mouse) {
    Scalar cmp = threshold;
    int index = 0;
    Integer min_index = null;
    for (Tensor point : control) {
      Scalar distance = Norm._2.between(point.extract(0, 2), mouse.extract(0, 2));
      if (Scalars.lessThan(distance, cmp)) {
        cmp = distance;
        min_index = index;
      }
      ++index;
    }
    return Optional.ofNullable(min_index);
  }
}
