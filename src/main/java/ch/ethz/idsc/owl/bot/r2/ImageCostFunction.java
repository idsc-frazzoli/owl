// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.Serializable;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.math.region.FlipYXTensorInterp;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.VectorQ;

public abstract class ImageCostFunction implements CostFunction, Serializable {
  private static final Tensor ORIGIN = Array.zeros(2).unmodifiable();
  // ---
  private final Tensor image;
  private final Tensor range;
  /* package for testing */
  final FlipYXTensorInterp<Scalar> flipYXTensorInterp;

  /** @param image as a matrix
   * @param range effective size of image in coordinate space
   * @param outside point member status */
  protected ImageCostFunction(Tensor image, Tensor range, Scalar outside) {
    this.image = MatrixQ.require(image);
    this.range = VectorQ.requireLength(range, 2);
    flipYXTensorInterp = new FlipYXTensorInterp<>(image, range, value -> value, outside);
  }

  @Override // from HeuristicFunction
  public final Scalar minCostToGoal(Tensor tensor) {
    return RealScalar.ZERO;
  }

  public final Tensor image() {
    return image.unmodifiable();
  }

  public final Tensor range() {
    return range.unmodifiable();
  }

  public final Tensor scale() {
    return flipYXTensorInterp.scale();
  }

  public static Tensor origin() {
    return ORIGIN;
  }
}
