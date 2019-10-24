// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Total;

public enum AffineAppend {
  ;
  /** @param vector
   * @return given vector with one entry appended so that sum of entries equals one */
  public static Tensor of(Tensor vector) {
    return vector.copy().append(RealScalar.ONE.subtract(Total.ofVector(vector)));
  }
}
