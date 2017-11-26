// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.owl.math.TensorScalarFunction;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

/** region {x | f(x) <= 0} defined by the overriding implicit function f
 * 
 * for instance, the function f can be the distance to
 * and obstacle:
 * <ul>
 * <li>positive when outside the obstacle,
 * <li>zero when in contact with the obstacle, and
 * <li>negative when in collision
 * </ul> */
public abstract class ImplicitFunctionRegion implements Region<Tensor>, TensorScalarFunction {
  @Override
  public final boolean isMember(Tensor tensor) {
    return Sign.isNegativeOrZero(apply(tensor));
  }
}
