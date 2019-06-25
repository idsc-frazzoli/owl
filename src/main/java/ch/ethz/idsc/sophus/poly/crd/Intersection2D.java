// code by jph
package ch.ethz.idsc.sophus.poly.crd;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.lie.Cross;

/* package */ enum Intersection2D {
  ;
  /** @param pos1
   * @param dir1
   * @param pos2
   * @param dir2
   * @return solution to the equation pos1 + lambda * dir1 == pos2 + mu * dir2 */
  public static Tensor of(Tensor pos1, Tensor dir1, Tensor pos2, Tensor dir2) {
    Tensor x2 = Cross.of(dir2);
    Scalar num = pos2.subtract(pos1).dot(x2).Get();
    Scalar den = dir1.dot(x2).Get();
    if (Scalars.isZero(den))
      throw TensorRuntimeException.of(dir1, dir2);
    return pos1.add(dir1.multiply(num.divide(den)));
  }
}
