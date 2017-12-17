// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Numerical Recipes 3rd Edition Section 17.3.1 */
class ModifiedMidpointIntegrator implements Integrator {
  @Override // from Integrator
  public Tensor step(Flow flow, Tensor x, Scalar h) {
    // LONGTERM implement
    throw new RuntimeException();
  }
}
