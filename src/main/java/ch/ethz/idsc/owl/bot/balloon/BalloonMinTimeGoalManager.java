// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.GoalAdapter;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

/** TODO ASTOLL comment inaccurate
 * heuristic adds max speed of available control to max norm of image gradient */
/* package */ class BalloonMinTimeGoalManager implements Region<Tensor>, CostFunction, Serializable {
  private final SphericalRegion sphericalRegion;
  /** unit of maxSpeed is velocity, e.g. [m/s] */
  private final Scalar maxSpeed;

  /** @param goal for vectors of length 2
   * @param radius
   * @param maxSpeed positive */
  public BalloonMinTimeGoalManager(Tensor goal, Scalar radius, Scalar maxSpeed) {
    sphericalRegion = new SphericalRegion(goal, radius);
    this.maxSpeed = Sign.requirePositive(maxSpeed);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return sphericalRegion.distance(x.extract(0, 2)).divide(maxSpeed); // unit [m] / [m/s] simplifies to [s]
  }

  @Override // from Region
  public boolean isMember(Tensor element) {
    return sphericalRegion.isMember(element.extract(0, 2));
  }

  @Override // from CostFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory); // unit [s]
  }

  public GoalInterface getGoalInterface() {
    return new GoalAdapter(SimpleTrajectoryRegionQuery.timeInvariant(this), this);
  }
}
