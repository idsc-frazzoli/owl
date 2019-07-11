// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Tan;

/** formula to convert steering angle to (front-)wheel angle
 * so that no friction arises in the ideal/no-slip scenario.
 * 
 * formula simplified from document by marcello and panos
 * 
 * see also
 * <a href="https://en.wikipedia.org/wiki/Ackermann_steering_geometry">Ackermann steering geometry</a> */
public class AckermannSteering implements Serializable {
  private final Scalar factor;

  /** function works with {@link Quantity}.
   * both input parameters are requires to have the same unit.
   * 
   * @param x_front non-zero distance from rear to front axis
   * @param y_offset distance from center of axis to tire */
  public AckermannSteering(Scalar x_front, Scalar y_offset) {
    if (Scalars.isZero(x_front))
      throw TensorRuntimeException.of(x_front, y_offset);
    factor = y_offset.divide(x_front);
  }

  /** @param delta
   * @return angle for a wheel located at (x_front, y_offset) */
  public Scalar angle(Scalar delta) {
    Scalar tan = Tan.of(delta);
    return ArcTan.of(tan.divide(RealScalar.ONE.subtract(tan.multiply(factor))));
  }

  /** @param delta
   * @return steering angle for two wheels located at +y_offset and -y_offset
   * as is the symmetric standard configuration of most vehicles */
  // implementation is redundant to angle function in order to reuse the computation of the tangent
  public Tensor pair(Scalar delta) {
    Scalar tan = Tan.of(delta);
    Scalar tan_factor = tan.multiply(factor);
    return Tensors.of( //
        ArcTan.of(tan.divide(RealScalar.ONE.subtract(tan_factor))), //
        ArcTan.of(tan.divide(RealScalar.ONE.add(tan_factor))));
  }
}
