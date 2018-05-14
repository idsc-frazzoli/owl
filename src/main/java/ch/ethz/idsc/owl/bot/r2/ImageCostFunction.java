// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.Floor;

/** current implementation uses 2d image to store costs
 * a given trajectory is mapped to the pixels and costs are
 * weighted according to the traverse time */
public class ImageCostFunction implements CostFunction, Serializable {
  private static final Tensor ORIGIN = Array.zeros(2).unmodifiable();

  /** @param image as a matrix
   * @param range effective size of image in coordinate space
   * @param outside point member status */
  public static CostFunction of(Tensor image, Tensor range, Scalar outside) {
    return new ImageCostFunction(image, range, outside);
  }

  // ---
  private final Tensor image;
  private final List<Integer> dimensions;
  private final Tensor range;
  private final Tensor scale;
  private final Scalar outside;
  private final int max_y;

  private ImageCostFunction(Tensor image, Tensor range, Scalar outside) {
    this.image = MatrixQ.require(image);
    dimensions = Dimensions.of(image);
    max_y = dimensions.get(0) - 1;
    this.range = VectorQ.requireLength(range, 2);
    scale = Tensors.vector(dimensions.get(1), dimensions.get(0)).pmul(range.map(Scalar::reciprocal));
    this.outside = outside;
  }

  /* package */ Scalar pointcost(Tensor tensor) {
    if (tensor.length() != 2)
      tensor = tensor.extract(0, 2);
    Tensor pixel = Floor.of(tensor.pmul(scale));
    // code features redundancies for instance to ImageRegion
    int pix = pixel.Get(0).number().intValue();
    if (0 <= pix && pix < dimensions.get(1)) {
      int piy = max_y - pixel.Get(1).number().intValue();
      if (0 <= piy && piy < dimensions.get(0))
        return image.Get(piy, pix);
    }
    return outside;
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    return RealScalar.ZERO;
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    Tensor dts = StateTimeTrajectories.deltaTimes(glcNode, trajectory);
    Tensor cost = Tensor.of(trajectory.stream() //
        .map(StateTime::state) //
        .map(this::pointcost));
    return cost.dot(dts).Get();
  }

  public Tensor image() {
    return image.unmodifiable();
  }

  public Tensor range() {
    return range.unmodifiable();
  }

  public Tensor origin() {
    return ORIGIN;
  }
}
