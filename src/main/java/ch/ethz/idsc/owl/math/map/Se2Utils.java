// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.SquareMatrixQ;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;
import ch.ethz.idsc.tensor.sca.Tan;

public enum Se2Utils {
  ;
  /** maps a vector x from the Lie-algebra se2 to a vector of the Lie-group SE2
   * 
   * @param x element in the se2 Lie-algebra of the form {vx, vy, beta}
   * @return element g in SE2 as vector with coordinates of g == exp x */
  public static Tensor exp(Tensor x) {
    Scalar be = x.Get(2);
    if (Scalars.isZero(be))
      return x.copy();
    Scalar vx = x.Get(0);
    Scalar vy = x.Get(1);
    Scalar cd = Cos.FUNCTION.apply(be).subtract(RealScalar.ONE);
    Scalar sd = Sin.FUNCTION.apply(be);
    return Tensors.of( //
        sd.multiply(vx).add(cd.multiply(vy)).divide(be), //
        sd.multiply(vy).subtract(cd.multiply(vx)).divide(be), //
        be);
  }

  /** @param g element in the SE2 Lie group of the form {px, py, beta}
   * @return element x in the se2 Lie algebra with x == log g, and g == exp x */
  public static Tensor log(Tensor g) {
    final Scalar be = g.Get(2);
    if (Scalars.isZero(be))
      return g.copy();
    Scalar x = g.Get(0);
    Scalar y = g.Get(1);
    Scalar be2 = be.divide(RealScalar.of(2));
    Scalar tan = Tan.FUNCTION.apply(be2);
    return Tensors.of(y.add(x.divide(tan)).multiply(be2), y.divide(tan).subtract(x).multiply(be2), be);
  }

  /** maps a vector from the group SE2 to a matrix in SE2
   * 
   * @param g = {px, py, angle}
   * @return matrix with dimensions 3x3
   * <pre>
   * [+Ca -Sa px]
   * [+Sa +Ca py]
   * [0 0 1]
   * </pre>
   * @throws Exception if parameter g is not a vector of length 3 */
  public static Tensor toSE2Matrix(Tensor xya) {
    Scalar angle = xya.Get(2);
    Scalar cos = Cos.FUNCTION.apply(angle);
    Scalar sin = Sin.FUNCTION.apply(angle);
    return Tensors.matrix(new Tensor[][] { //
        { cos, sin.negate(), xya.Get(0) }, //
        { sin, cos /*----*/, xya.Get(1) }, //
        { RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE }, //
    });
  }

  /** @param vector of the form {px, py}
   * @return
   * <pre>
   * [1 0 px]
   * [0 1 py]
   * [0 0 1]
   * </pre> */
  public static Tensor toSE2Translation(Tensor xy) {
    return Tensors.matrix(new Tensor[][] { //
        { RealScalar.ONE, RealScalar.ZERO, xy.Get(0) }, //
        { RealScalar.ZERO, RealScalar.ONE, xy.Get(1) }, //
        { RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE }, //
    });
  }

  /** maps a matrix from the group SE2 to a vector in the group SE2
   * 
   * @param matrix
   * @return */
  public static Tensor fromSE2Matrix(Tensor matrix) { // only used in tests
    SquareMatrixQ.require(matrix);
    return Tensors.of(matrix.Get(0, 2), matrix.Get(1, 2), //
        ArcTan.of(matrix.Get(0, 0), matrix.Get(1, 0))); // arc tan is numerically stable
  }
}
