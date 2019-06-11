// code by ob, jph
package ch.ethz.idsc.sophus.lie.se2c;

import ch.ethz.idsc.sophus.lie.se2.Se2Differences;
import ch.ethz.idsc.sophus.math.PseudoDistance;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;

public enum Se2CoveringPseudoDistance implements PseudoDistance {
  INSTANCE;
  // ---
  @Override // from PseudoDistance
  public Tensor pseudoDistance(Tensor p, Tensor q) {
    Tensor tensor = Se2Differences.INSTANCE.pair(p, q);
    return Tensors.of( //
        Norm._2.ofVector(tensor.extract(0, 2)), //
        tensor.Get(2).abs());
  }
}
