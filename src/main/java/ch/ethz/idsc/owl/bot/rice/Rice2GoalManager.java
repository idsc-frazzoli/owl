// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.RadiusXY;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.sca.Ramp;

/** Careful: implementation assumes max speed == 1
 * Cost function attains values as minimal distance (not minimal time)! */
/* package */ class Rice2GoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  // ---
  private final Tensor center;
  private final Scalar radius;

  public Rice2GoalManager(EllipsoidRegion ellipsoidRegion) {
    super(new TimeInvariantRegion(ellipsoidRegion));
    center = Extract2D.FUNCTION.apply(ellipsoidRegion.center());
    this.radius = RadiusXY.requireSame(ellipsoidRegion.radius()); // x-y radius have to be equal
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    Scalar minDist = Ramp.of(Vector2Norm.between(Extract2D.FUNCTION.apply(x), center).subtract(radius));
    return minDist; // .divide(1 [m/s]), since max velocity == 1 => division is obsolete
  }
}
