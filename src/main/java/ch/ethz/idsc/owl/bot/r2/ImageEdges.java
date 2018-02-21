// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;

public enum ImageEdges {
  ;
  private static final Scalar TFF = RealScalar.of(255);
  private static final Scalar MAX = RealScalar.of(256);

  private static boolean isBlackOrWhite(Scalar scalar) {
    return Scalars.isZero(scalar) || scalar.equals(TFF);
  }

  /** @param image with entries 0, or 255
   * @param ttl less than 256
   * @return */
  public static Tensor extrusion(Tensor image, final int ttl) {
    if (!image.flatten(-1).map(Scalar.class::cast).allMatch(ImageEdges::isBlackOrWhite))
      throw TensorRuntimeException.of(image);
    Scalar factor = MAX.divide(RealScalar.of(ttl + 1));
    return FloodFill2D.of(image, ttl).multiply(factor).add(image);
  }
}
