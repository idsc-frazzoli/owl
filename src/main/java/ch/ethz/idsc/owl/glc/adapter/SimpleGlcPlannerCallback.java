// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.TrajectorySample;

/** TODO JPH class name too generic */
public class SimpleGlcPlannerCallback implements GlcPlannerCallback {
  private final TrajectoryEntity trajectoryEntity;
  private boolean showCost = false;

  public SimpleGlcPlannerCallback(TrajectoryEntity trajectoryEntity) {
    this.trajectoryEntity = trajectoryEntity;
  }

  @Override // from GlcPlannerCallback
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      if (showCost)
        System.out.println("Cost to Goal: " + optional.get().costFromRoot());
      List<TrajectorySample> tail = //
          GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.get());
      trajectoryEntity.trajectory(Trajectories.glue(head, tail));
    }
  }

  public void showCost() {
    showCost = true;
  }
}
