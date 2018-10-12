// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Rodrigues;

public enum So3Exponential implements LieExponential {
  INSTANCE;
  // ---
  @Override // from LieExponential
  public Tensor exp(Tensor vector) {
    return Rodrigues.exp(vector);
  }

  @Override // from LieExponential
  public Tensor log(Tensor matrix) {
    return Rodrigues.log(matrix);
  }
}
