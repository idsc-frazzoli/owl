// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.sophus.TensorMetric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

// TODO JPH cite reference
public class ShepardInterpolation {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);
  // ---
  private final Chop chop = Chop._10;
  private final ScalarUnaryOperator power = Power.function(2);
  private final TensorMetric tensorMetric;
  private final BiinvariantMean biinvariantMean;

  public ShepardInterpolation(TensorMetric tensorMetric, BiinvariantMean biinvariantMean) {
    this.tensorMetric = tensorMetric;
    this.biinvariantMean = biinvariantMean;
  }

  public Tensor weights(Tensor sequence, Tensor point) {
    Tensor weights = Unprotect.empty(sequence.length());
    int count = 0;
    for (Tensor tensor : sequence) {
      Scalar dist = tensorMetric.distance(tensor, point);
      if (chop.allZero(dist))
        return UnitVector.of(sequence.length(), count);
      // TODO omit power here
      weights.append(power.apply(dist).reciprocal());
      ++count;
    }
    return NORMALIZE.apply(weights);
  }

  public Tensor at(Tensor sequence, Tensor point) {
    return biinvariantMean.mean(sequence, weights(sequence, point));
  }
}
