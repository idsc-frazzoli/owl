// code by jph
package ch.ethz.idsc.owl.bot.lv;

import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** the distance used in the ellipsoid is Euclidean.
 * perhaps more suitable for the state space model would be a logarithmic distance */
/* package */ class LvGoalInterface extends SimpleTrajectoryRegionQuery implements GoalInterface {
  public static GoalInterface create(Tensor center, Tensor radius) {
    return new LvGoalInterface(new EllipsoidRegion(center, radius));
  }

  public LvGoalInterface(EllipsoidRegion ellipsoidRegion) {
    super(new TimeInvariantRegion(ellipsoidRegion));
    if (!VectorQ.ofLength(ellipsoidRegion.center(), 2))
      throw TensorRuntimeException.of(ellipsoidRegion.center());
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }
}
