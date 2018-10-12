// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;

public enum RnExponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Tensor exp(Tensor x) {
    return x.copy();
  }

  @Override // from LieExponential
  public Tensor log(Tensor g) {
    return g.copy();
  }
}
