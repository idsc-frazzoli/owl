// code by jph
package ch.ethz.idsc.demo.math3;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Append;
import ch.ethz.idsc.tensor.red.Total;

public enum AffineAppend {
  ;
  /** @param vector
   * @return given vector with one entry appended so that sum of entries equals one */
  public static Tensor of(Tensor vector) {
    return Append.of(vector, RealScalar.ONE.subtract(Total.ofVector(vector)));
  }
}
