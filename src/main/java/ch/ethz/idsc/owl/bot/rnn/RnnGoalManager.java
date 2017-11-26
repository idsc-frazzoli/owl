// code by jph
package ch.ethz.idsc.owl.bot.rnn;

import java.util.List;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.noise.ContinuousNoise;
import ch.ethz.idsc.owl.math.noise.ContinuousNoiseUtils;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.Sign;

/** cost is a varying distance metric */
class RnnGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  private final ContinuousNoise continuousNoise;

  public RnnGoalManager(Tensor center, Scalar radius) {
    super(new TimeInvariantRegion(new EllipsoidRegion(center, Array.of(l -> radius, center.length()))));
    GlobalAssert.that(VectorQ.ofLength(center, 2));
    continuousNoise = ContinuousNoiseUtils.wrap2D(SimplexContinuousNoise.FUNCTION);
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    Scalar sum = trajectory.stream().map(StateTime::state).map(continuousNoise).reduce(Scalar::add).get();
    sum = sum.add(RealScalar.of(trajectory.size()));
    GlobalAssert.that(Sign.isPositiveOrZero(sum));
    return sum;
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }
}
