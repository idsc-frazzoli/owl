// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;

/** in Euclidean space
 * the exponential function is the identity
 * the logarithm function is the identity */
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
