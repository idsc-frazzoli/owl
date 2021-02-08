// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import java.io.Serializable;

import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

public abstract class TwdFlows implements FlowsInterface, Serializable {
  // ---
  private final Scalar maxSpeedHalf;
  private final Scalar halfWidth;

  /** @param maxSpeed [m*s^-1]
   * @param halfWidth [m*rad^-1] */
  public TwdFlows(Scalar maxSpeed, Scalar halfWidth) {
    maxSpeedHalf = maxSpeed.multiply(RationalScalar.HALF);
    this.halfWidth = halfWidth;
  }

  /** @param speedL in the interval [-1, 1] without unit
   * @param speedR in the interval [-1, 1] without unit
   * @return */
  protected final Tensor singleton(Scalar speedL, Scalar speedR) {
    Scalar speed = speedL.add(speedR).multiply(maxSpeedHalf);
    Scalar rate = speedR.subtract(speedL).multiply(maxSpeedHalf).divide(halfWidth);
    return N.DOUBLE.of(Tensors.of(speed, speed.zero(), rate));
  }
}
