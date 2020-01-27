// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public class DiscreteIntegrator implements StateIntegrator {
  private final StateSpaceModel stateSpaceModel;

  public DiscreteIntegrator(StateSpaceModel stateSpaceModel) {
    this.stateSpaceModel = Objects.requireNonNull(stateSpaceModel);
  }

  @Override // from StateIntegrator
  public List<StateTime> trajectory(StateTime stateTime, Flow flow) {
    Tensor xn = stateSpaceModel.f(stateTime.state(), flow.getU());
    return Collections.singletonList(new StateTime(xn, stateTime.time().add(RealScalar.ONE)));
  }
}
