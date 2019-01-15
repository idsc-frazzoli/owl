// code by jph
package ch.ethz.idsc.owl.bot.tn;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.tensor.Tensor;

class TnIdentityWrap implements CoordinateWrap {
  @Override // from CoordinateWrap
  public Tensor represent(Tensor x) {
    return x.copy();
  }

  @Override // from TensorDifference
  public Tensor difference(Tensor p, Tensor q) {
    return q.subtract(p);
  }
}
