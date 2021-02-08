// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.List;

import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** {@link SimpleEpisodeIntegrator} takes the largest possible time step for integration.
 * 
 * implementation is fast and should only be applied for simple {@link StateSpaceModel}s */
public class SimpleEpisodeIntegrator extends AbstractEpisodeIntegrator {
  public SimpleEpisodeIntegrator(StateSpaceModel stateSpaceModel, Integrator integrator, StateTime stateTime) {
    super(stateSpaceModel, integrator, stateTime);
  }

  @Override // from AbstractEpisodeIntegrator
  protected List<StateTime> abstract_move(Tensor flow, Scalar period) {
    return FixedStateIntegrator.create(integrator, stateSpaceModel, period, 1).trajectory(tail(), flow);
  }
}
