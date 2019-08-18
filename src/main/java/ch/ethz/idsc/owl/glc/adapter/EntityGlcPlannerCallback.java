// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.TrajectorySample;

public class EntityGlcPlannerCallback implements GlcPlannerCallback {
  public static GlcPlannerCallback of(TrajectoryEntity trajectoryEntity) {
    return new EntityGlcPlannerCallback(trajectoryEntity, false);
  }

  public static GlcPlannerCallback verbose(TrajectoryEntity trajectoryEntity) {
    return new EntityGlcPlannerCallback(trajectoryEntity, true);
  }

  // ---
  private final TrajectoryEntity trajectoryEntity;
  private final boolean showCost;

  private EntityGlcPlannerCallback(TrajectoryEntity trajectoryEntity, boolean showCost) {
    this.trajectoryEntity = Objects.requireNonNull(trajectoryEntity);
    this.showCost = showCost;
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
}
