// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import ch.ethz.idsc.owl.math.ImageGradient;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.itp.Interpolation;
import ch.ethz.idsc.tensor.itp.LinearInterpolation;
import ch.ethz.idsc.tensor.itp.NearestInterpolation;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.N;

/** rotated gradient of potential function */
public class ImageGradientInterpolation implements Serializable {
  private static final Tensor ZEROS = Tensors.vectorDouble(0, 0).unmodifiable();

  /** @param image
   * @param range
   * @param amp
   * @return high quality continuous interpolation (slower than nearest) */
  public static ImageGradientInterpolation linear(Tensor image, Tensor range, Scalar amp) {
    return new ImageGradientInterpolation(image, range, amp, LinearInterpolation::of);
  }

  /** @param image
   * @param range
   * @param amp
   * @return fast discontinuous interpolation (fast) */
  public static ImageGradientInterpolation nearest(Tensor image, Tensor range, Scalar amp) {
    return new ImageGradientInterpolation(image, range, amp, NearestInterpolation::of);
  }

  /***************************************************/
  private final Tensor scale;
  private final Interpolation interpolation;
  private final Scalar maxNormGradient;

  /** @param render with rank 2. For instance, Dimensions.of(image) == [179, 128]
   * @param range with length() == 2
   * @param amp factor */
  private ImageGradientInterpolation(Tensor _image, Tensor range, Scalar amp, Function<Tensor, Interpolation> function) {
    Tensor image = _displayOrientation(_image);
    MatrixQ.require(image);
    List<Integer> dims = Dimensions.of(image);
    scale = Tensors.vector(dims).pmul(range.map(Scalar::reciprocal));
    Tensor field = N.DOUBLE.of(ImageGradient.rotated(image)).multiply(amp);
    interpolation = function.apply(field);
    maxNormGradient = field.flatten(1).map(Vector2Norm::of).reduce(Max::of).get();
  }

  /** @param vector of length 2
   * @return potentially unmodifiable */
  public Tensor get(Tensor vector) {
    Tensor index = vector.pmul(scale);
    try {
      return interpolation.get(index);
    } catch (Exception exception) {
      // index is out of bounds
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
