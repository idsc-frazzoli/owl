// code by jph
package ch.ethz.idsc.owl.math.region;

import java.awt.geom.AffineTransform;

import ch.ethz.idsc.tensor.Tensor;

/** @see AffineTransform */
/* package */ class AffineFrame {
  private final double m00;
  private final double m10;
  private final double m01;
  private final double m11;
  private final double m02;
  private final double m12;

  /** @param matrix of dimensions 3 x 3 */
  public AffineFrame(Tensor matrix) {
    m00 = matrix.Get(0, 0).number().doubleValue();
    m10 = matrix.Get(1, 0).number().doubleValue();
    m01 = matrix.Get(0, 1).number().doubleValue();
    m11 = matrix.Get(1, 1).number().doubleValue();
    m02 = matrix.Get(0, 2).number().doubleValue();
    m12 = matrix.Get(1, 2).number().doubleValue();
  }

  /** @param px
   * @param py
   * @return */
  public double toX(double px, double py) {
    return m00 * px + m01 * py + m02;
  }

  public double toY(double px, double py) {
    return m10 * px + m11 * py + m12;
  }
}
