// code by jph
package ch.ethz.idsc.owl.data.img;

import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public enum ImageBlackWhiteQ {
  ;
  private static final Scalar TFF = RealScalar.of(255);

  /** @param image
   * @return true if image contains only 0 or 255 */
  public static boolean of(Tensor image) {
    return members(image).stream().allMatch(ImageBlackWhiteQ::isBlackOrWhite);
  }

  /** @param image
   * @return
   * @throws Exception if image contains entries different from 0 or 255 */
  public static Tensor require(Tensor image) {
    if (!of(image))
      throw new RuntimeException("image not a black and white: " + members(image));
    return image;
  }

  // helper function
  private static boolean isBlackOrWhite(Scalar scalar) {
    return Scalars.isZero(scalar) || scalar.equals(TFF);
  }

  // helper function
  private static List<Scalar> members(Tensor image) {
    return image.flatten(-1).map(Scalar.class::cast) //
        .distinct().sorted().collect(Collectors.toList());
  }
}
