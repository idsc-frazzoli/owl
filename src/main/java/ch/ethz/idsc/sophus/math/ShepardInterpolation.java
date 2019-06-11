// code by jph
package ch.ethz.idsc.sophus.math;

import java.io.Serializable;

import ch.ethz.idsc.sophus.TensorMetric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;

// TODO JPH cite reference
public class ShepardInterpolation implements Serializable {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);
  private static final Chop CHOP = Chop._11;
  // ---
  private final TensorMetric tensorMetric;
  private final BiinvariantMean biinvariantMean;

  public ShepardInterpolation(TensorMetric tensorMetric, BiinvariantMean biinvariantMean) {
    this.tensorMetric = tensorMetric;
    this.biinvariantMean = biinvariantMean;
  }

  public Tensor weights(Tensor domain, Tensor point) {
    Tensor weights = Unprotect.empty(domain.length());
    int count = 0;
    for (Tensor tensor : domain) {
      Scalar distance = tensorMetric.distance(tensor, point);
      if (CHOP.allZero(distance))
        return UnitVector.of(domain.length(), count);
      weights.append(distance.reciprocal());
      ++count;
    }
    return NORMALIZE.apply(weights);
  }

  public Tensor at(Tensor domain, Tensor values, Tensor point) {
    return biinvariantMean.mean(values, weights(domain, point));
  }
}
