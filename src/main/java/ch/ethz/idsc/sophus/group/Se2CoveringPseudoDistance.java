// code by ob, jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.math.PseudoDistance;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;

public enum Se2CoveringPseudoDistance implements PseudoDistance {
  INSTANCE;
  // ---
  private static final LieDifferences LIE_DIFFERENCES = //
      new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);

  @Override
  public Tensor pseudoDistance(Tensor p, Tensor q) {
    Tensor tensor = LIE_DIFFERENCES.pair(p, q);
    return Tensors.of( //
        Norm._2.ofVector(tensor.extract(0, 2)), //
        tensor.Get(2).abs());
  }
}
