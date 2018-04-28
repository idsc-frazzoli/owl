// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;

/** class that wraps a given {@link TrajectoryRegionQuery} to an obstacle constraint.
 * non-empty intersection of the trajectory with the region represents a constraint
 * violation. */
public class TrajectoryObstacleConstraint implements PlannerConstraint, Serializable {
  private final TrajectoryRegionQuery trajectoryRegionQuery;

  public TrajectoryObstacleConstraint(TrajectoryRegionQuery trajectoryRegionQuery) {
    this.trajectoryRegionQuery = trajectoryRegionQuery;
  }

  @Override
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return !trajectoryRegionQuery.firstMember(trajectory).isPresent();
  }
}
