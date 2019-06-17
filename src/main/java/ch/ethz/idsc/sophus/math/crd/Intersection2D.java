// code by jph
package ch.ethz.idsc.sophus.math.crd;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Cross;

/* package */ enum Intersection2D {
  ;
  /** @param p1
   * @param n1
   * @param p2
   * @param n2
   * @return solution to the equation p1 + lambda * n1 == p2 + mu * n2 */
  public static Tensor of(Tensor p1, Tensor n1, Tensor p2, Tensor n2) {
    Tensor x2 = Cross.of(n2);
    Scalar num = p2.subtract(p1).dot(x2).Get();
    Scalar den = n1.dot(x2).Get();
    return p1.add(n1.multiply(num.divide(den)));
  }
}
