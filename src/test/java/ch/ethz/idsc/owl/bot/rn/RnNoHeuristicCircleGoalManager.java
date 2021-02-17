// code by jph and jl
package ch.ethz.idsc.owl.bot.rn;

import java.util.List;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.region.BallRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.sca.Sign;

/** objective is minimum path length
 * path length is measured in Euclidean distance
 * 
 * functionality for testing purpose only */
public class RnNoHeuristicCircleGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  /** constructor creates a spherical region in R^n with given center and radius.
   * distance measure is Euclidean distance.
   * 
   * @param center vector with length == n
   * @param radius positive */
  public RnNoHeuristicCircleGoalManager(Tensor center, Scalar radius) {
    super(new TimeInvariantRegion(new BallRegion(center, Sign.requirePositive(radius))));
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    StateTime from = glcNode.stateTime();
    return Vector2Norm.between(from.state(), Lists.getLast(trajectory).state());
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }
}