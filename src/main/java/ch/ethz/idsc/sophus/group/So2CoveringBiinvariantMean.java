// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum So2CoveringBiinvariantMean implements ScalarBiinvariantMean {
  INSTANCE;
  // ---
  @Override // from ScalarBiinvariantMean
  public Scalar mean(Tensor sequence, Tensor weights) {
    return AffineQ.require(weights).dot(So2Helper.rangeQ(sequence)).Get();
  }
}
