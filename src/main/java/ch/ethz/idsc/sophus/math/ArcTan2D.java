// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.ArcTan;

public enum ArcTan2D {
  ;
  /** @param vector of the form {x, y, ...}
   * @return ArcTan[x, y] */
  public static Scalar of(Tensor vector) {
    return ArcTan.of(vector.Get(0), vector.Get(1));
  }
}
