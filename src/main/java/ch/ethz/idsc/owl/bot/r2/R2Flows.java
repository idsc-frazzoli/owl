// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.sca.Sign;

/** for single integrator state space
 * use with {@link EulerIntegrator} */
public class R2Flows implements FlowsInterface, Serializable {
  private static final StateSpaceModel SINGLE_INTEGRATOR = SingleIntegratorStateSpaceModel.INSTANCE;
  // ---
  private final Scalar speed;

  public R2Flows(Scalar speed) {
    this.speed = Sign.requirePositive(speed);
  }

  @Override // from FlowsInterface
  public Collection<Flow> getFlows(int resolution) {
    GlobalAssert.that(2 < resolution); // otherwise does not cover plane
    List<Flow> list = new ArrayList<>();
    for (Tensor u : CirclePoints.of(resolution))
      list.add(StateSpaceModels.createFlow(SINGLE_INTEGRATOR, u.multiply(speed)));
    return list;
  }

  public Flow stayPut() {
    return StateSpaceModels.createFlow(SINGLE_INTEGRATOR, Array.zeros(2));
  }
}
