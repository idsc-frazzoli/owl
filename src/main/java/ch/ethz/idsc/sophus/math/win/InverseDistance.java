// code by jph
package ch.ethz.idsc.sophus.math.win;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.NormalizeTotal;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.sca.Chop;

public class InverseDistance implements Serializable {
  private static final Chop CHOP = Chop._14;
  // ---
  private final TensorMetric tensorMetric;

  /** @param tensorMetric non-null */
  public InverseDistance(TensorMetric tensorMetric) {
    this.tensorMetric = Objects.requireNonNull(tensorMetric);
  }

  public Tensor weights(Tensor tensor, Tensor q) {
    Tensor weights = Unprotect.empty(tensor.length());
    int count = 0;
    for (Tensor p : tensor) {
      Scalar distance = tensorMetric.distance(p, q);
      if (CHOP.allZero(distance))
        return UnitVector.of(tensor.length(), count);
      weights.append(distance.reciprocal());
      ++count;
    }
    return NormalizeTotal.FUNCTION.apply(weights);
  }
}
