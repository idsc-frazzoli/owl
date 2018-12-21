// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;

public enum St1CoveringIntegrator implements LieIntegrator {
  INSTANCE;
  // ---
  /** @param g == {lambda, t}
   * @param x == {plambda, pt}
   * @return g . exp x */
  @Override // from LieIntegrator
  public Tensor spin(Tensor g, Tensor x) {
    return new St1CoveringGroupElement(g).combine(St1CoveringExponential.INSTANCE.exp(x));
  }
}
