// code by jph
package ch.ethz.idsc.owl.bot.sat;

import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class SatelliteGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  public static GoalInterface create(Tensor center, Tensor radius) {
    return new SatelliteGoalManager(new EllipsoidRegion(center, radius));
  }
  // ---
  // private final Tensor center;
  // private final Scalar radius;

  // TODO implementation assumes max speed == 1
  public SatelliteGoalManager(EllipsoidRegion ellipsoidRegion) {
    super(new TimeInvariantRegion(ellipsoidRegion));
    // center = ellipsoidRegion.center();
    // this.radius =
    // RadiusXY.requireSame(ellipsoidRegion.radius()); // x-y radius have to be equal
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }
}
