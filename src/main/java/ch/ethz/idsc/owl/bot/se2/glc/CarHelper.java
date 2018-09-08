// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

public enum CarHelper {
  ;
  /** the turning radius of the flow is the reciprocal of the given rate
   * 
   * @param speed, positive for forward, and negative for backward, unit [m/s]
   * @param ratePerMeter of turning, unit [rad*m^-1]
   * @return flow with u == {speed[m*s^-1], 0.0, (rate*speed)[rad*s^-1]} */
  public static Flow singleton(Scalar speed, Tensor ratePerMeter) {
    return StateSpaceModels.createFlow(Se2StateSpaceModel.INSTANCE, //
        N.DOUBLE.of(Tensors.of(speed, RealScalar.ZERO, ratePerMeter.Get().multiply(speed))));
  }
}
