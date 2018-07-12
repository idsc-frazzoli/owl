// code by jph, ynager
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

public enum Tse2CarHelper {
  ;
  /** the turning radius of the flow is the reciprocal of the given rate
   * 
   * @param speed, positive for forward, and negative for backward, unit [m/s]
   * @param rate of turning, unit [rad/m]
   * @return flow with u == {speed[m*s^-1], 0.0, (rate*speed)[rad*s^-1]} */
  public static Flow singleton(Scalar speed, Tensor rate) {
    return StateSpaceModels.createFlow(TSe2StateSpaceModel.INSTANCE, //
        N.DOUBLE.of(Tensors.of(speed, RealScalar.ZERO, rate.Get().multiply(speed), RealScalar.ZERO)));
  }
}
