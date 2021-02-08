// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.num.Rationalize;

/** for single integrator state space
 * use with {@link EulerIntegrator} */
public class R2RationalFlows extends R2Flows {
  private static final ScalarUnaryOperator RATIONALIZE = Rationalize.withDenominatorLessEquals(100);

  /** @param speed */
  public R2RationalFlows(Scalar speed) {
    super(speed);
  }

  @Override // from R2Flows
  protected Tensor mapU(Tensor u) {
    return u.map(RATIONALIZE);
  }
}
