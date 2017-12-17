// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Bulirsch Stoer method
 * 
 * Numerical Recipes 3rd Edition Section 17.3.2 */
enum BulirschStoerIntegrator implements Integrator {
  INSTANCE;
  // ---
  @Override // from Integrator
  public Tensor step(Flow flow, Tensor x, Scalar h) {
    // LONGTERM implement
    throw new RuntimeException();
  }
}
