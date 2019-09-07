// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.sophus.crv.CurveDecimation;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum Se2CurveDecimation {
  ;
  /** @param epsilon
   * @return */
  public static TensorUnaryOperator of(Scalar epsilon) {
    return CurveDecimation.of(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE::log, epsilon);
  }
}
