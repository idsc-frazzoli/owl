// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.sophus.lie.LieIntegrator;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.tensor.Tensor;

// TODO special case of lie euler integrator
public enum Se2Integrator implements LieIntegrator {
  INSTANCE;
  // ---
  /** @param g == {px, py, alpha}
   * @param x == {vx, vy, beta}
   * @return g . exp x */
  @Override // from LieIntegrator
  public Tensor spin(Tensor g, Tensor x) {
    return new Se2GroupElement(g).combine(Se2CoveringExponential.INSTANCE.exp(x));
  }
}
