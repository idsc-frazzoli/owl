// code by jl
package ch.ethz.idsc.owl.glc.adapter;

import ch.ethz.idsc.owl.math.CoordinateWrap;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

public enum IdentityWrap implements CoordinateWrap {
  INSTANCE;
  // ---
  @Override
  public Tensor represent(Tensor x) {
    return x.copy();
  }

  @Override
  public Scalar distance(Tensor p, Tensor q) {
    return Norm.INFINITY.between(p, q);
  }
}
