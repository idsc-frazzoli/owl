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

  @Override // from Integrator
  public Tensor step(Flow flow, Tensor x0, Scalar _2h) {
    Scalar h = _2h.multiply(RationalScalar.HALF);
    Tensor xm = x0.add(flow.at(x0).multiply(h)); // h
    return /**/ x0.add(flow.at(xm).multiply(_2h)); // 2h
  }
}
