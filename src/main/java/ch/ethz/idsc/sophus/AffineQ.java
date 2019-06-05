// code by ob, jph
package ch.ethz.idsc.sophus;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;

public enum AffineQ {
  ;
  private static final Chop CHOP = Chop._12;

  /** @param vector
   * @throws Exception if scalar entries of given mask do not add up to one */
  public static void require(Tensor vector) {
    CHOP.requireClose(Total.of(vector), RealScalar.ONE);
  }

  /** @param vector
   * @throws Exception if scalar entries of given vector do not add up to one
   * @throws Exception if either scalar entry in given vector is negative */
  public static void requirePositive(Tensor vector) {
    Scalar sum = vector.stream() //
        .map(Scalar.class::cast) //
        .map(Sign::requirePositiveOrZero) //
        .reduce(Scalar::add).get();
    CHOP.requireClose(sum, RealScalar.ONE);
  }
}
