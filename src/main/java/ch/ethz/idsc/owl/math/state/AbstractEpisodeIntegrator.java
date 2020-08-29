// code by jph
package ch.ethz.idsc.owl.math.state;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class AbstractEpisodeIntegrator implements EpisodeIntegrator, Serializable {
  protected final StateSpaceModel stateSpaceModel;
  /* package */ final Integrator integrator;
  private StateTime stateTime;

  public AbstractEpisodeIntegrator(StateSpaceModel stateSpaceModel, Integrator integrator, StateTime stateTime) {
    this.stateSpaceModel = stateSpaceModel;
    this.integrator = integrator;
    this.stateTime = stateTime;
  }

  /** @param flow
   * @param period
   * @return */
  protected abstract List<StateTime> abstract_move(Tensor flow, Scalar period);

  @Override // from EpisodeIntegrator
  public final void move(Tensor u, Scalar now) {
    List<StateTime> trajectory = abstract_move(u, now.subtract(stateTime.time()));
    stateTime = Lists.getLast(trajectory);
  }

  @Override
  public final StateTime tail() {
    return stateTime;
  }
}
