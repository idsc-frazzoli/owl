// code by jph
package ch.ethz.idsc.owl.math.region;

import java.awt.geom.AffineTransform;
import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

/** fast implementation of SE(2) x R^2 -> R^2 action
 * for use in BufferedImageRegion
 * 
 * @see AffineTransform */
// TODO the redundancy with AffineFrame2D is unfortunate
/* package */ class AffineFrame implements Serializable {
  private final double m00;
  private final double m01;
  private final double m02;
  private final double m10;
  private final double m11;
  private final double m12;

  /** @param matrix of dimensions 3 x 3 */
  public AffineFrame(Tensor matrix) {
    Tensor row0 = matrix.get(0);
    m00 = row0.Get(0).number().doubleValue();
    m01 = row0.Get(1).number().doubleValue();
    m02 = row0.Get(2).number().doubleValue();
    Tensor row1 = matrix.get(1);
    m10 = row1.Get(0).number().doubleValue();
    m11 = row1.Get(1).number().doubleValue();
    m12 = row1.Get(2).number().doubleValue();
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
