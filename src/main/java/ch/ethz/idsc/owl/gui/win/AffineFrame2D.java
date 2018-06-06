// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** @see AffineTransform */
/* package */ class AffineFrame2D {
  private final Tensor tensor;
  private final double m00;
  private final double m10;
  private final double m01;
  private final double m11;
  private final double m02;
  private final double m12;

  /** @param tensor 3 x 3 matrix */
  public AffineFrame2D(Tensor tensor) {
    this.tensor = tensor.copy();
    m00 = tensor.Get(0, 0).number().doubleValue();
    m10 = tensor.Get(1, 0).number().doubleValue();
    m01 = tensor.Get(0, 1).number().doubleValue();
    m11 = tensor.Get(1, 1).number().doubleValue();
    m02 = tensor.Get(0, 2).number().doubleValue();
    m12 = tensor.Get(1, 2).number().doubleValue();
  }

  /** @param point of which the first 2 entries are interpreted as x, and y
   * @return */
  public Point2D toPoint2D(Tensor point) {
    return toPoint2D( //
        point.Get(0).number().doubleValue(), //
        point.Get(1).number().doubleValue());
  }

  /** @param px
   * @param py
   * @return */
  public Point2D toPoint2D(double px, double py) {
    return new Point2D.Double( //
        m00 * px + m01 * py + m02, //
        m10 * px + m11 * py + m12);
  }

  /** @param point
   * @return vector of length 2 */
  public Tensor toVector(Tensor point) {
    return toVector( //
        point.Get(0).number().doubleValue(), //
        point.Get(1).number().doubleValue());
  }

  /** @param px
   * @param py
   * @return vector of length 2 */
  public Tensor toVector(double px, double py) {
    return Tensors.vectorDouble( //
        m00 * px + m01 * py + m02, //
        m10 * px + m11 * py + m12);
  }

  /** @param matrix with dimensions 3 x 3
   * @return combined transformation of this and given matrix */
  public AffineFrame2D dot(Tensor matrix) {
    return new AffineFrame2D(tensor.dot(matrix));
  }

  /** @return 3 x 3 matrix that represents this transformation */
  public Tensor tensor_copy() {
    return tensor.copy();
  }
}
