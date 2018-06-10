// code by jph
package ch.ethz.idsc.owl.glc.std;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.math.state.TrajectorySample;

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
