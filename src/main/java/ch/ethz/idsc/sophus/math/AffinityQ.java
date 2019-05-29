// code by ob
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Sign;

public enum AffinityQ {
  ;
  public static void requirePositive(Tensor weights) {
    weights.stream().map(Scalar.class::cast).forEach(Sign::requirePositiveOrZero);
    if (!Total.of(weights).equals(RealScalar.ONE)) {
      System.out.println(Total.of(weights) + " Application of Biinvariant mean not valid! (sum of weights not 1");
      throw TensorRuntimeException.of(weights);
    }
  }
}
