// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.noise.ContinuousNoise;
import ch.ethz.idsc.owl.math.noise.ContinuousNoiseUtils;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Ramp;

/** cost function with unit time
 * 
 * the cost increment may be zero therefore, min cost to goal also is zero */
public class R2NoiseCostFunction implements CostFunction, Serializable {
  private static final long serialVersionUID = 4192352535614661893L;
  private static final ContinuousNoise CONTINUOUS_NOISE = //
      ContinuousNoiseUtils.wrap2D(SimplexContinuousNoise.FUNCTION);
  // ---
  private final Scalar treshold;

  public R2NoiseCostFunction(Scalar treshold) {
    this.treshold = treshold;
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    Tensor dts = StateTimeTrajectories.deltaTimes(glcNode, trajectory);
    Tensor cost = Tensor.of(trajectory.stream() //
        .map(StateTime::state) //
        .map(CONTINUOUS_NOISE) //
        .map(scalar -> scalar.subtract(treshold)) //
        .map(Ramp.FUNCTION));
    return cost.dot(dts).Get();
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    return RealScalar.ZERO;
  }
}
