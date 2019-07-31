// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixQ;

public enum MatrixBracket {
  ;
  /** @param x square matrix
   * @param y square matrix
   * @return Lie-bracket [x, y] == x.y - y.x
   * @throws Exception if x or y are not square matrices */
  public static Tensor of(Tensor x, Tensor y) {
    return MatrixQ.require(x).dot(y).subtract(MatrixQ.require(y).dot(x));
  }
}
