// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** fifth-order Runge-Kutta formula based on RK4
 * implementation requires 12 flow evaluations
 * 
 * Numerical Recipes 3rd Edition (17.2.3)
 * 
 * class is a simple reference implementation for testing.
 * use {@link RungeKutta45Integrator} for applications */
/* package */ enum RungeKutta45Reference implements Integrator {
  INSTANCE;
  // ---
  private static final Scalar HALF = RationalScalar.of(1, 2);
  private static final Scalar W1 = RationalScalar.of(-1, 15);
  private static final Scalar W2 = RationalScalar.of(16, 15);

  @Override
  public Tensor step(Flow flow, Tensor x, Scalar h) {
    Tensor y1 = RungeKutta4Integrator.increment(flow, x, h);
    Scalar h2 = h.multiply(HALF);
    Tensor xm = RungeKutta4Integrator.INSTANCE.step(flow, x, h2);
    Tensor y2 = RungeKutta4Integrator.INSTANCE.step(flow, xm, h2).subtract(x);
    Tensor ya = y1.multiply(W1).add(y2.multiply(W2));
    return x.add(ya);
  }
}
