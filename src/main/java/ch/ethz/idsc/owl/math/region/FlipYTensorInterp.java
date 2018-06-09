// code by jph
package ch.ethz.idsc.owl.math.region;

import java.util.List;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** the motivation for the coordinate ordering is
 * that the input image tensor looks the same when printed in the console
 * as when rendered as grayscale image on screen
 * 
 * however, this requires to flip the y coordinate when extracting coordinates */
// API class name may not be ideal
public class FlipYTensorInterp<T> {
  private final Tensor image;
  private final int dim1;
  private final float scaleX;
  private final float scaleY;
  private final T outside;
  private final int max_y;
  private final Function<Scalar, T> function;

  public FlipYTensorInterp(Tensor image, Tensor range, Function<Scalar, T> function, T outside) {
    this.image = image;
    List<Integer> dimensions = Dimensions.of(image);
    dim1 = dimensions.get(1);
    max_y = dimensions.get(0) - 1;
    VectorQ.requireLength(range, 2);
    Tensor scale = Tensors.vector(dimensions.get(1), dimensions.get(0)).pmul(range.map(Scalar::reciprocal));
    scaleX = scale.Get(0).number().floatValue();
    scaleY = scale.Get(1).number().floatValue();
    this.outside = outside;
    this.function = function;
  }

  /** @param vector of length at least 2
   * @return */
  public T at(Tensor vector) {
    float fix = vector.Get(0).number().floatValue();
    if (0 <= fix) {
      float fiy = vector.Get(1).number().floatValue();
      if (0 <= fiy) {
        int pix = (int) (fix * scaleX);
        if (pix < dim1) {
          int piy = max_y - (int) (fiy * scaleY);
          if (0 <= piy)
            return function.apply(image.Get(piy, pix));
        }
      }
    }
    return outside;
  }
}
