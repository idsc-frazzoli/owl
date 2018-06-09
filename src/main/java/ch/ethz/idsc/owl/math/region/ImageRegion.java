// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.MatrixQ;

/** only the first two coordinates are tested for membership
 * a location is available if the grayscale value of the pixel equals 0 */
public class ImageRegion implements Region<Tensor>, Serializable {
  private static final Tensor ORIGIN = Array.zeros(2).unmodifiable();
  // ---
  private final Tensor image;
  private final Tensor range;
  private final Tensor scale;
  private final FlipYXTensorInterp<Boolean> flipYXTensorInterp;

  /** @param image has to be a matrix
   * @param range effective size of image in coordinate space, vector of length 2
   * @param outside point member status */
  public ImageRegion(Tensor image, Tensor range, boolean outside) {
    this.image = MatrixQ.require(image);
    List<Integer> dimensions = Dimensions.of(image);
    int dim0 = dimensions.get(0);
    int dim1 = dimensions.get(1);
    this.range = range;
    scale = Tensors.vector(dim1, dim0).pmul(range.map(Scalar::reciprocal));
    flipYXTensorInterp = new FlipYXTensorInterp<>(image, range, Scalars::nonZero, outside);
  }

  @Override // from Region
  public boolean isMember(Tensor tensor) {
    return flipYXTensorInterp.at(tensor);
  }

  public Tensor image() {
    return image.unmodifiable();
  }

  public Tensor range() {
    return range.unmodifiable();
  }

  public Tensor scale() {
    return scale.unmodifiable();
  }

  public Tensor origin() {
    return ORIGIN;
  }
}
