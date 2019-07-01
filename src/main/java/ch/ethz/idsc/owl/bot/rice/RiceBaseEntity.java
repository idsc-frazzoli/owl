// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.List;

import ch.ethz.idsc.owl.ani.api.AbstractCircularEntity;
import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.glc.core.GlcTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ren.TreeRender;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm2Squared;

/* package */ abstract class RiceBaseEntity extends AbstractCircularEntity implements GlcPlannerCallback {
  protected final TreeRender treeRender = new TreeRender();

  public RiceBaseEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
  }

  @Override
  public final Scalar distance(Tensor x, Tensor y) {
    return Norm2Squared.between(x, y); // non-negative
  }

  @Override
  public final void expandResult(List<TrajectorySample> head, GlcTrajectoryPlanner trajectoryPlanner) {
    treeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }
}
