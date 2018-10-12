// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.owl.math.group.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.group.Se2CoveringGroupElement;
import ch.ethz.idsc.tensor.Tensor;

/** measures difference between p and q in SE(2) covering group relative to p
 * difference(p, q) = Inv[p] . q */
public enum Se2CoveringWrap implements CoordinateWrap {
  INSTANCE;
  // ---
  @Override // from CoordinateWrap
  public Tensor represent(Tensor x) {
    return x;
  }

  @Override // from TensorDifference
  public Tensor difference(Tensor p, Tensor q) {
    Tensor tensor = new Se2CoveringGroupElement(p).inverse().combine(q);
    return Se2CoveringExponential.INSTANCE.log(tensor);
  }
}
