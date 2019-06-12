// code by jph
package ch.ethz.idsc.sophus.lie.se2c;

import ch.ethz.idsc.sophus.lie.LieIntegrator;
import ch.ethz.idsc.tensor.Tensor;

public enum Se2CoveringIntegrator implements LieIntegrator {
  INSTANCE;
  // ---
  /** @param g == {px, py, alpha}
   * @param x == {vx, vy, beta}
   * @return g . exp x */
  @Override // from LieIntegrator
  public Tensor spin(Tensor g, Tensor x) {
    return new Se2CoveringGroupElement(g).combine(Se2CoveringExponential.INSTANCE.exp(x));
  }
}
