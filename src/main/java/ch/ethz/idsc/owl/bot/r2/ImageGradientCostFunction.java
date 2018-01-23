// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.red.VectorAngle;

/* package */ class ImageGradientCostFunction implements CostFunction, Serializable {
  private final ImageGradientInterpolation imageGradientInterpolation;

  public ImageGradientCostFunction(ImageGradientInterpolation imageGradientInterpolation) {
    this.imageGradientInterpolation = imageGradientInterpolation;
  }

  /** @param tensor with entries {px, py, alpha}
   * @return */
  private Scalar pointcost(Tensor tensor) {
    Tensor p = tensor.extract(0, 2); // xy
    Tensor v = imageGradientInterpolation.get(p);
    Tensor u = AngleVector.of(tensor.Get(2)); // orientation
    return VectorAngle.of(u, v).orElse(RealScalar.ZERO); // LONGTERM perhaps this can be simplified
  }

  @Override // from CostFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    Tensor dts = Trajectories.deltaTimes(glcNode, trajectory);
    Tensor cost = Tensor.of(trajectory.stream() //
        .map(StateTime::state) //
        .map(this::pointcost));
    return cost.dot(dts).Get(); // .multiply(RealScalar.of(20.0));
  }

  @Override // from CostFunction
  public Scalar minCostToGoal(Tensor tensor) {
    return RealScalar.ZERO;
  }
}
