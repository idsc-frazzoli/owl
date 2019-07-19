// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.sophus.lie.se2.Se2Integrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** exact integration of flow using matrix exponential and logarithm.
 * states are encoded in the default coordinates of the se2 Lie-algebra. */
public enum Se2FlowIntegrator implements Integrator {
  INSTANCE;
  // ---
  /** Parameter description:
   * g in SE2
   * h in R */
  @Override // from Integrator
  public Tensor step(Flow flow, Tensor g, Scalar h) {
    return Se2Integrator.INSTANCE.spin(g, flow.getU().multiply(h));
  }
}
