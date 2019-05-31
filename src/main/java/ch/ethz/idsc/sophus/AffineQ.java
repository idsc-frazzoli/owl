// code by ob
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

  public static void require(Tensor mask) {
    CHOP.requireClose(Total.of(mask), RealScalar.ONE);
  }

  public static void requirePositive(Tensor mask) {
    Scalar sum = mask.stream() //
        .map(Scalar.class::cast) //
        .map(Sign::requirePositiveOrZero) //
        .reduce(Scalar::add).get();
    CHOP.requireClose(sum, RealScalar.ONE);
  }
}
