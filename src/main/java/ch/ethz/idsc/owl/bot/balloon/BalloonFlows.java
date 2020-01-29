// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModels;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.N;

/* package */ class BalloonFlows implements FlowsInterface, Serializable {
  /** @param u_max with units [K * s^-1]
   * @return new ApFlows instance */
  public static FlowsInterface of(Scalar u_max, StateSpaceModel stateSpaceModel) {
    return new BalloonFlows(u_max, stateSpaceModel);
  }

  // ---
  private final Scalar u_max;
  private final StateSpaceModel stateSpaceModel;

  private BalloonFlows(Scalar u_max, StateSpaceModel stateSpaceModel) {
    this.u_max = u_max;
    this.stateSpaceModel = stateSpaceModel;
  }

  @Override // from FlowsInterface
  public Collection<Flow> getFlows(int resolution) {
    Collection<Flow> collection = new ArrayList<>();
    for (Tensor u : Subdivide.of(u_max.negate(), u_max, 1 + resolution))
      collection.add(StateSpaceModels.createFlow(stateSpaceModel, N.DOUBLE.of(Tensors.of(u))));
    return collection;
  }
}
