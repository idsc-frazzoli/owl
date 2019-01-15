// code by jph
package ch.ethz.idsc.owl.ani.api;

import java.util.List;

import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Tensor;

public abstract class AbstractRrtsEntity extends TrajectoryEntity {
  public AbstractRrtsEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
  }

  public abstract void startPlanner( //
      RrtsPlannerCallback rrtsPlannerCallback, List<TrajectorySample> head, Tensor goal);
}
