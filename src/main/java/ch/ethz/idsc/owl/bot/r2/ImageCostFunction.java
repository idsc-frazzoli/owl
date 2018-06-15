// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.FlipYXTensorInterp;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** current implementation uses 2d image to store costs
 * a given trajectory is mapped to the pixels and costs are
 * weighted according to the traverse time */
public class ImageCostFunction implements CostFunction, Serializable {
  private static final Tensor ORIGIN = Array.zeros(2).unmodifiable();
  // ---
  private final Tensor image;
  private final Tensor range;
  /* public for testing */
  public final FlipYXTensorInterp<Scalar> flipYXTensorInterp;

  /** @param image as a matrix
   * @param range effective size of image in coordinate space
   * @param outside point member status */
  public ImageCostFunction(Tensor image, Tensor range, Scalar outside) {
    this.image = MatrixQ.require(image);
    this.range = VectorQ.requireLength(range, 2);
    flipYXTensorInterp = new FlipYXTensorInterp<>(image, range, value -> value, outside);
  }

  @Override // from HeuristicFunction
  public final Scalar minCostToGoal(Tensor tensor) {
    return RealScalar.ZERO;
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    Tensor dts = StateTimeTrajectories.deltaTimes(glcNode, trajectory);
    Tensor cost = Tensor.of(trajectory.stream() //
        .map(StateTime::state) //
        .map(flipYXTensorInterp::at));
    return cost.dot(dts).Get();
  }

  public final Tensor image() {
    return image.unmodifiable();
  }

  public final Tensor range() {
    return range.unmodifiable();
  }

  public Tensor origin() {
    return ORIGIN;
  }
}
