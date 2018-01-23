// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** 2nd order RungeKutta
 * integrator requires 2 flow evaluations
 * 
 * Numerical Recipes 3rd Edition (17.1.2) */
public enum MidpointIntegrator implements Integrator {
  INSTANCE;
  // ---
  @Override // from Integrator
  public Tensor step(Flow flow, Tensor x, Scalar h) {
    Tensor k1 = flow.at(x).multiply(h);
    Tensor k2 = flow.at(x.add(k1.multiply(RationalScalar.HALF))).multiply(h);
    return x.add(k2);
  }
}
