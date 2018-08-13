// code by jl, jph
package ch.ethz.idsc.owl.glc.adapter;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.tensor.Tensor;

public enum IdentityWrap implements CoordinateWrap {
  INSTANCE;
  // ---
  @Override // from CoordinateWrap
  public Tensor represent(Tensor x) {
    return x.copy();
  }

  @Override // from TensorDifference
  public Tensor difference(Tensor p, Tensor q) {
    return q.subtract(p);
  }
}
