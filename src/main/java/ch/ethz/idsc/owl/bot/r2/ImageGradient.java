// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.math.planar.Cross2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.TensorMap;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.opt.NearestInterpolation;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.N;

/** rotated gradient of potential function */
public class ImageGradient implements Serializable {
  /** @param image
   * @param range
   * @param amp
   * @return high quality continuous interpolation (slower than nearest) */
  public static ImageGradient linear(Tensor image, Tensor range, Scalar amp) {
    return new ImageGradient(image, range, amp, LinearInterpolation::of);
  }

  /** @param image
   * @param range
   * @param amp
   * @return fast discontinuous interpolation (fast) */
  public static ImageGradient nearest(Tensor image, Tensor range, Scalar amp) {
    return new ImageGradient(image, range, amp, NearestInterpolation::of);
  }
  // ---

  private static final Tensor ZEROS = Tensors.vectorDouble(0, 0).unmodifiable();
  // ---
  private final Tensor scale;
  private final Interpolation interpolation;
  private final Scalar maxNormGradient;
  // ---
  public final Tensor field_copy; // TODO testing only

  /** @param image with rank 2. For instance, Dimensions.of(image) == [179, 128]
   * @param range with length() == 2
   * @param amp factor */
  private ImageGradient(Tensor _image, Tensor range, Scalar amp, Function<Tensor, Interpolation> function) {
    Tensor image = _displayOrientation(_image);
    GlobalAssert.that(MatrixQ.of(image));
    List<Integer> dims = Dimensions.of(image);
    scale = Tensors.vector(dims).pmul(range.map(Scalar::reciprocal));
    Tensor diffx = Differences.of(image);
    diffx = TensorMap.of(tensor -> tensor.extract(0, dims.get(1) - 1), diffx, 1);
    Tensor diffy = TensorMap.of(Differences::of, image, 1);
    diffy = diffy.extract(0, dims.get(0) - 1);
    Tensor field = Transpose.of(Tensors.of(diffx, diffy), 2, 0, 1);
    field = TensorMap.of(Cross2D::of, field, 2).multiply(amp);
    field = N.DOUBLE.of(field);
    interpolation = function.apply(field);
    maxNormGradient = field.flatten(1).map(Norm._2::ofVector).reduce(Max::of).get();
    // ---
    field_copy = field.copy();
  }

  /** @param vector of length 2
   * @return potentially unmodifiable */
  public Tensor rotate(Tensor vector) {
    Tensor index = vector.pmul(scale);
    try {
      return interpolation.get(index);
    } catch (Exception exception) {
      // ---
    }
    return ZEROS;
  }

  /** @return max(||gradient||) */
  public Scalar maxNormGradient() {
    return maxNormGradient;
  }

  // helper function
  private static Tensor _displayOrientation(Tensor tensor) {
    return Transpose.of(Reverse.of(tensor)); // flip y's, then swap x-y
  }
}
