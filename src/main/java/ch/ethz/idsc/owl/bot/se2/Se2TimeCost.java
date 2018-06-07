package ch.ethz.idsc.owl.bot.se2;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Max;

public class Se2TimeCost implements CostFunction, Serializable {
  public static final Se2TimeCost of(Se2ComboRegion se2ComboRegion, Collection<Flow> controls) {
    return new Se2TimeCost(se2ComboRegion, controls);
  }

  private final Scalar maxSpeed;
  private final Scalar maxTurning;
  Se2ComboRegion se2ComboRegion;

  public Se2TimeCost(Se2ComboRegion se2ComboRegion, Collection<Flow> controls) {
    this.se2ComboRegion = se2ComboRegion;
    this.maxSpeed = Se2Controls.maxSpeed(controls);
    this.maxTurning = Se2Controls.maxTurning(controls);
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    return Max.of( //
        se2ComboRegion.d_xy(x).divide(maxSpeed), //
        se2ComboRegion.d_angle(x).divide(maxTurning));
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }
}
