// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.bot.tse2.Tse2StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

public enum ApHelper {
  ;
  /** the turning radius of the flow is the reciprocal of the given rate
   * 
   * @param angle of attack AOA [rad]
   * @param thrust [N]
   * @return flow with u == { AOA[rad*m^-1], thrusts[N]} */
  public static Flow singleton(Scalar aoa, Scalar thrusts) {
    return StateSpaceModels.createFlow( //
        Tse2StateSpaceModel.INSTANCE, N.DOUBLE.of(Tensors.of(aoa, thrusts)));
  }
}
 