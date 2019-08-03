// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** @see AffineTransform */
/* package */ class AffineFrame2D {
  private final Tensor matrix;
  private final double m00;
  private final double m10;
  private final double m01;
  private final double m11;
  private final double m02;
  private final double m12;

  /** @param matrix of dimensions 3 x 3 */
  public AffineFrame2D(Tensor matrix) {
    this.matrix = matrix.copy();
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
  public Point2D toPoint2D(double px, double py) {
    return new Point2D.Double( //
        m00 * px + m01 * py + m02, //
        m10 * px + m11 * py + m12);
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
    return new AffineFrame2D(this.matrix.dot(matrix));
  }

  /** @return determinant of affine transform, for a standard,
   * right-hand coordinate system, the determinant is negative */
  public double det() {
    return m00 * m11 - m10 * m01;
  }

  /** @return 3 x 3 matrix that represents this transformation */
  public Tensor matrix_copy() {
    return matrix.copy();
  }
}
