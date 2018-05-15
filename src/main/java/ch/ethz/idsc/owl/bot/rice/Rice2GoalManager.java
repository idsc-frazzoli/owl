// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.RadiusXY;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.state.StandardTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Ramp;

public class Rice2GoalManager extends StandardTrajectoryRegionQuery implements GoalInterface {
  public static GoalInterface create(Tensor center, Tensor radius) {
    return new Rice2GoalManager(new EllipsoidRegion(center, radius));
  }

  // ---
  private final Tensor center;
  private final Scalar radius;

  // TODO implementation assumes max speed == 1
  public Rice2GoalManager(EllipsoidRegion ellipsoidRegion) {
    super(new TimeInvariantRegion(ellipsoidRegion));
    center = ellipsoidRegion.center();
    this.radius = RadiusXY.requireSame(ellipsoidRegion.radius()); // x-y radius have to be equal
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    Tensor pc = x.extract(0, 2);
    Tensor pd = center.extract(0, 2);
    Scalar mindist = Ramp.of(Norm._2.between(pc, pd).subtract(radius));
    return mindist; // .divide(1 [m/s]), since max velocity == 1 => division is obsolete
  }
}
