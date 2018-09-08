// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** exact integration of flow using exponential map and logarithm.
 * states are encoded in the default coordinates of the se2 Lie-algebra.
 * 
 * Important: u is assumed to be of the form u == {vx, 0, rate}
 * 
 * Se2CarIntegrator is approximately
 * 3x faster than {@link RungeKutta4Integrator}
 * 11x faster than {@link RungeKutta45Integrator} */
public enum Se2CarIntegrator implements Integrator {
  INSTANCE;
  // ---
  /** Parameter description:
   * g in SE2
   * h in R */
  @Override // from Integrator
  public Tensor step(Flow flow, Tensor g, Scalar h) {
    // u is assumed to be of the form u == {vx[m*s^-1], 0, rate[rad*s^-1]}
    return Se2CarLieIntegrator.INSTANCE.spin(g, flow.getU().multiply(h));
  }
}
