// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** fifth-order Runge-Kutta formula based on RK4
 * implementation requires 11 flow evaluations
 * 
 * Numerical Recipes 3rd Edition (17.2.3) */
public enum RungeKutta45Integrator implements Integrator {
  INSTANCE;
  // ---
  private static final Scalar HALF = RationalScalar.HALF;
  private static final Scalar THIRD = RationalScalar.of(1, 3);
  private static final Scalar SIXTH = RationalScalar.of(1, 6);
  // ---
  private static final Scalar W1 = RationalScalar.of(-1, 15);
  private static final Scalar W2 = RationalScalar.of(16, 15);

  @Override // from Integrator
  public Tensor step(Flow flow, Tensor x, Scalar h) {
    Tensor y1;
    final Tensor flow_at_x = flow.at(x); // used twice
    {
      Tensor k1 = flow_at_x.multiply(h); // euler increment
      Tensor k2 = flow.at(x.add(k1.multiply(HALF))).multiply(h);
      Tensor k3 = flow.at(x.add(k2.multiply(HALF))).multiply(h);
      Tensor k4 = flow.at(x.add(k3)).multiply(h);
      y1 = k1.add(k4).multiply(SIXTH).add(k2.add(k3).multiply(THIRD));
    }
    Scalar h2 = h.multiply(HALF);
    Tensor xm;
    {
      Tensor k1 = flow_at_x.multiply(h2); // euler increment
      Tensor k2 = flow.at(x.add(k1.multiply(HALF))).multiply(h2);
      Tensor k3 = flow.at(x.add(k2.multiply(HALF))).multiply(h2);
      Tensor k4 = flow.at(x.add(k3)).multiply(h2);
      Tensor incr = k1.add(k4).multiply(SIXTH).add(k2.add(k3).multiply(THIRD));
      xm = x.add(incr);
    }
    Tensor y2 = RungeKutta4Integrator.INSTANCE.step(flow, xm, h2).subtract(x);
    Tensor ya = y1.multiply(W1).add(y2.multiply(W2));
    return x.add(ya);
  }
}
