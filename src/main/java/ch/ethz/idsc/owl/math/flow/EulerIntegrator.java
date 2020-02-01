// code by bapaden and jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Numerical Recipes 3rd Edition (17.1.1) */
public enum EulerIntegrator implements Integrator {
  INSTANCE;

  @Override // from Integrator
  public Tensor step(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h) {
    return x.add(stateSpaceModel.f(x, u).multiply(h));
  }
}
