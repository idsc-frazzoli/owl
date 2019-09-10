// code by jph
package ch.ethz.idsc.sophus.hs.s2;

import ch.ethz.idsc.sophus.crv.LineDistance;
import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum S2Line implements LineDistance {
  INSTANCE;
  // ---
  @Override // from LineDistance
  public TensorNorm tensorNorm(Tensor p, Tensor q) {
    return new S2LineDistance(p, q);
  }
}
