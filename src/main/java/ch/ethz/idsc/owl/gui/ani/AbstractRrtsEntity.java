// code by jph
package ch.ethz.idsc.owl.gui.ani;

import java.util.List;

import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Tensor;

public abstract class AbstractRrtsEntity extends TrajectoryEntity {
  public AbstractRrtsEntity(EpisodeIntegrator episodeIntegrator) {
    super(null, null); // FIXME
    // super(episodeIntegrator);
  }

  public abstract void startPlanner( //
      RrtsTrajectoryPlannerCallback trajectoryPlannerCallback, List<TrajectorySample> head, Tensor goal);
}
