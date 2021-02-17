// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.List;

import ch.ethz.idsc.owl.ani.api.AbstractCircularEntity;
import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ren.TreeRender;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Vector2NormSquared;

/* package */ abstract class RiceBaseEntity extends AbstractCircularEntity implements GlcPlannerCallback {
  protected final TreeRender treeRender = new TreeRender();

  public RiceBaseEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
  }

  @Override
  public final Scalar distance(Tensor x, Tensor y) {
    return Vector2NormSquared.between(x, y); // non-negative
  }

  @Override
  public final void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    treeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }
}
