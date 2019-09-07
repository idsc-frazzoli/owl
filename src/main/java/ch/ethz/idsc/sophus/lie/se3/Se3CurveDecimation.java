// code by jph
package ch.ethz.idsc.sophus.lie.se3;

import ch.ethz.idsc.sophus.crv.CurveDecimation;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Flatten;

public enum Se3CurveDecimation {
  ;
  /** @param epsilon
   * @return */
  public static CurveDecimation of(Scalar epsilon) {
    return CurveDecimation.of(Se3Group.INSTANCE, g -> Flatten.of(Se3Exponential.INSTANCE.log(g)), epsilon);
  }
}
