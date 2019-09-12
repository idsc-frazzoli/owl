// code by jph
package ch.ethz.idsc.sophus.hs.s2;

import ch.ethz.idsc.sophus.crv.CurveDecimation;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Sign;

public enum S2CurveDecimation {
  ;
  /** @param epsilon
   * @return */
  public static CurveDecimation of(Scalar epsilon) {
    return CurveDecimation.of(S2Line.INSTANCE, Sign.requirePositiveOrZero(epsilon));
  }
}
