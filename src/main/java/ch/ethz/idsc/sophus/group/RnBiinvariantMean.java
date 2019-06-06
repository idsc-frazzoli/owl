// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.tensor.Tensor;

public enum RnBiinvariantMean implements BiinvariantMeanInterface {
  INSTANCE;
  // ---
  @Override
  public Tensor mean(Tensor sequence, Tensor weights) {
    return AffineQ.require(weights).dot(sequence);
  }
}
