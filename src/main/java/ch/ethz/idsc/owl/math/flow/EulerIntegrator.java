// code by bapaden and jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Numerical Recipes 3rd Edition (17.1.1) */
public enum EulerIntegrator implements Integrator {
  INSTANCE;
  // ---
  @Override // from Integrator
  public Tensor step(Flow flow, Tensor x, Scalar h) {
    return x.add(flow.at(x).multiply(h));
  }
}
