// code by jph
package ch.ethz.idsc.owl.bot.lv;

import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** the distance used in the ellipsoid is Euclidean.
 * perhaps more suitable for the state space model would be a logarithmic distance */
/* package */ class LvGoalInterface extends SimpleTrajectoryRegionQuery implements GoalInterface {
  /** @param center
   * @param radius
   * @return */
  public static GoalInterface create(Tensor center, Tensor radius) {
    return new LvGoalInterface(new EllipsoidRegion(center, radius));
  }

  /***************************************************/
  public LvGoalInterface(EllipsoidRegion ellipsoidRegion) {
    super(new TimeInvariantRegion(ellipsoidRegion));
    VectorQ.requireLength(ellipsoidRegion.center(), 2);
  }

  @Override // from GoalInterface
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from GoalInterface
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }
}
