// code by jph, ynager
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

/* package */ enum Tse2CarHelper {
  ;
  /** the turning radius of the flow is the reciprocal of the given rate
   * 
   * @param rate of turning [m^-1]
   * @param acceleration [m*s^-2]
   * @return flow with u == { rate[m^-1], acceleration[m*s^-2]} */
  public static Flow singleton(Scalar rate, Scalar acceleration) {
    return StateSpaceModels.createFlow( //
        Tse2StateSpaceModel.INSTANCE, N.DOUBLE.of(Tensors.of(rate, acceleration)));
  }
}
