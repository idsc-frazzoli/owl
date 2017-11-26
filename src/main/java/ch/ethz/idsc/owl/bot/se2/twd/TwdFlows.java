// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

public abstract class TwdFlows implements FlowsInterface {
  private final Scalar maxSpeed;
  private final Scalar halfWidth;

  /** @param maxSpeed [m*s^-1]
   * @param halfWidth [m*rad^-1] */
  public TwdFlows(Scalar maxSpeed, Scalar halfWidth) {
    this.maxSpeed = maxSpeed;
    this.halfWidth = halfWidth;
  }

  /** @param speedL
   * @param speedR
   * @return */
  protected final Flow singleton(Scalar speedL, Scalar speedR) {
    Scalar speed = speedL.add(speedR).multiply(maxSpeed).divide(RealScalar.of(2));
    Scalar rate = speedL.subtract(speedR).multiply(maxSpeed).divide(RealScalar.of(2)).divide(halfWidth);
    return StateSpaceModels.createFlow(Se2StateSpaceModel.INSTANCE, //
        N.DOUBLE.of(Tensors.of(speed, RealScalar.ZERO, rate)));
  }
}
