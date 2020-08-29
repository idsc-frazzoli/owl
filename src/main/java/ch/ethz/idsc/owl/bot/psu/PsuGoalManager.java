// code by jph
package ch.ethz.idsc.owl.bot.psu;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.GoalAdapter;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class PsuGoalManager implements Region<Tensor>, CostFunction, Serializable {
  /** @param coordinateWrap
   * @param center
   * @param radius
   * @return */
  public static GoalInterface of(TensorMetric coordinateWrap, Tensor center, Tensor radius) {
    PsuGoalManager psuGoalManager = new PsuGoalManager(coordinateWrap, center, radius);
    return new GoalAdapter( //
        CatchyTrajectoryRegionQuery.timeInvariant(psuGoalManager), //
        psuGoalManager);
  }

  /***************************************************/
  private final TensorMetric coordinateWrap;
  private final Tensor center;
  private final Tensor radius;

  private PsuGoalManager(TensorMetric coordinateWrap, Tensor center, Tensor radius) {
    this.coordinateWrap = coordinateWrap;
    this.center = center;
    this.radius = radius;
  }

  @Override // from CostFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from CostFunction
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }

  @Override // from Region<Tensor>
  public boolean isMember(Tensor x) {
    return Sign.isNegative(coordinateWrap.distance(x, center).subtract(radius));
  }
}
