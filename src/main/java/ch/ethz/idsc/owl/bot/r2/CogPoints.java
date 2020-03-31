// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Flatten;

public enum CogPoints {
  ;
  /** the first coordinate is always {1, 0}.
   * the orientation of the points is counter-clockwise.
   * 
   * @param n
   * @param hi
   * @param lo
   * @return (n * 4) x 2 matrix */
  public static Tensor of(int n, Scalar hi, Scalar lo) {
    return Flatten.of(ConstantArray.of(Tensors.of(hi, hi, lo, lo), n)).pmul(CirclePoints.of(n * 4));
  }
}
