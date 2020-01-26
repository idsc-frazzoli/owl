// code by jph
package ch.ethz.idsc.owl.math.flow;

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

  static final Tensor increment(Flow flow, Tensor x, Scalar h) {
    Tensor k1 = flow.at(x).multiply(h); // euler increment
    Tensor k2 = flow.at(x.add(k1.multiply(HALF))).multiply(h);
    Tensor k3 = flow.at(x.add(k2.multiply(HALF))).multiply(h);
    Tensor k4 = flow.at(x.add(k3)).multiply(h);
    return k1.add(k4).multiply(SIXTH).add(k2.add(k3).multiply(THIRD));
  }

  @Override // from Integrator
  public Tensor step(Flow flow, Tensor x, Scalar h) {
    return x.add(increment(flow, x, h));
  }
}
