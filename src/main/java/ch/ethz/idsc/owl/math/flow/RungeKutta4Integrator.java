// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** fourth-order Runge-Kutta formula
 * integrator requires 4 flow evaluations
 * 
 * Numerical Recipes 3rd Edition (17.1.3) */
public enum RungeKutta4Integrator implements Integrator {
  INSTANCE;

  private static final Scalar HALF = RationalScalar.HALF;
  private static final Scalar THIRD = RationalScalar.of(1, 3);
  private static final Scalar SIXTH = RationalScalar.of(1, 6);

  static final Tensor increment(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h) {
    Tensor k1 = stateSpaceModel.f(x, u).multiply(h); // euler increment
    Tensor k2 = stateSpaceModel.f(x.add(k1.multiply(HALF)), u).multiply(h);
    Tensor k3 = stateSpaceModel.f(x.add(k2.multiply(HALF)), u).multiply(h);
    Tensor k4 = stateSpaceModel.f(x.add(k3), u).multiply(h);
    return k1.add(k4).multiply(SIXTH).add(k2.add(k3).multiply(THIRD));
  }

  @Override // from Integrator
  public Tensor step(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h) {
    return x.add(increment(stateSpaceModel, x, u, h));
  }
}
