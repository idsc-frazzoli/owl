// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.r2.AngleVector;
import ch.ethz.idsc.tensor.red.VectorAngle;

/** class is not used */
/* package */ class ImageGradientCostFunction implements CostFunction, Serializable {
  // ---
  private final ImageGradientInterpolation imageGradientInterpolation;

  public ImageGradientCostFunction(ImageGradientInterpolation imageGradientInterpolation) {
    this.imageGradientInterpolation = imageGradientInterpolation;
  }

  /** @param tensor with entries {px, py, alpha}
   * @return */
  private Scalar pointcost(Tensor tensor) {
    Tensor p = Extract2D.FUNCTION.apply(tensor); // xy
    Tensor v = imageGradientInterpolation.get(p);
    Tensor u = AngleVector.of(tensor.Get(2)); // orientation
    return VectorAngle.of(u, v).orElse(RealScalar.ZERO); // LONGTERM perhaps this can be simplified
  }

  @Override // from CostFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    Tensor dts = StateTimeTrajectories.deltaTimes(glcNode, trajectory);
    Tensor cost = Tensor.of(trajectory.stream() //
        .map(StateTime::state) //
        .map(this::pointcost));
    return (Scalar) cost.dot(dts);
  }

  @Override // from CostFunction
  public Scalar minCostToGoal(Tensor tensor) {
    return RealScalar.ZERO;
  }
}
