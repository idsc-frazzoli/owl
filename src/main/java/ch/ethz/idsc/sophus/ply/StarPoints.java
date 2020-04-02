// code by jph
package ch.ethz.idsc.sophus.ply;

import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Flatten;

public enum StarPoints {
  ;
  /** the orientation of the points is counter-clockwise.
   * 
   * @param n
   * @param hi
   * @param lo
   * @return (n * 2) x 2 matrix */
  public static Tensor of(int n, Scalar hi, Scalar lo) {
    return Flatten.of(ConstantArray.of(Tensors.of(hi, lo), n)).pmul(CirclePoints.of(n * 2));
  }

  /** @param n
   * @param hi
   * @param lo
   * @return */
  public static Tensor of(int n, Number hi, Number lo) {
    return of(n, RealScalar.of(hi), RealScalar.of(lo));
  }
}
