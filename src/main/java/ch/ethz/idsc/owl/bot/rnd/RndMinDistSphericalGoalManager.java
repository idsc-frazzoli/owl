// code by jph
package ch.ethz.idsc.owl.bot.rnd;

import java.util.List;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Sign;

public class RndMinDistSphericalGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  private final Tensor center;
  private final Scalar radius;

  public RndMinDistSphericalGoalManager(Tensor center, Scalar radius) {
    super(new TimeInvariantRegion(RndAndRegion.trivial_1(new SphericalRegion(center, radius))));
    GlobalAssert.that(Sign.isPositive(radius));
    this.center = center;
    this.radius = radius;
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    RndState rndState = RndState.of(x);
    // max(0, ||x - center|| - radius)
    return Ramp.of(Norm._2.between(rndState.x1, center).subtract(radius));
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    RndState rndState1 = RndState.of(glcNode.stateTime().state());
    RndState rndState2 = RndState.of(Lists.getLast(trajectory).state());
    return Norm._2.between(rndState1.x1, rndState2.x1);
  }
}
