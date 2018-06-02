// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.CirclePoints;

/* package */ class DeltaFlows implements FlowsInterface, Serializable {
  private final StateSpaceModel stateSpaceModel;
  private final Scalar amp;

  public DeltaFlows(StateSpaceModel stateSpaceModel, Scalar amp) {
    this.stateSpaceModel = stateSpaceModel;
    this.amp = amp;
  }

  @Override // from FlowsInterface
  public Collection<Flow> getFlows(int resolution) {
    Collection<Flow> collection = new ArrayList<>();
    for (Tensor u : CirclePoints.of(resolution))
      collection.add(StateSpaceModels.createFlow(stateSpaceModel, u.multiply(amp)));
    return collection;
  }
}
