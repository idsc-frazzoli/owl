// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public enum ImageEdges {
  ;
  private static final Scalar TFF = RealScalar.of(255);
  private static final Scalar MAX = RealScalar.of(256);

  private static boolean isBlackOrWhite(Scalar scalar) {
    return Scalars.isZero(scalar) || scalar.equals(TFF);
  }

  /** @param image with entries equals to 0 or 255
   * @param ttl less than 256
   * @return
   * @throws Exception if image contains entries different from 0 or 255 */
  public static Tensor extrusion(Tensor image, final int ttl) {
    requireBlackOrWhite(image);
    Scalar factor = MAX.divide(RealScalar.of(ttl + 1));
    return FloodFill2D.of(image, ttl).multiply(factor).add(image);
  }

  private static void requireBlackOrWhite(Tensor image) {
    List<Scalar> list = image.flatten(-1) //
        .map(Scalar.class::cast) //
        .distinct().sorted().collect(Collectors.toList());
    if (!list.stream().allMatch(ImageEdges::isBlackOrWhite))
      throw new RuntimeException("image not a black and white: " + list);
  }
}
