// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.owl.math.SignedDistanceFunction;
import ch.ethz.idsc.tensor.sca.Sign;

/** region {x | f(x) <= 0} defined by the overriding
 * {@link SignedDistanceFunction#signedDistance(Object)}
 * 
 * for instance, the function f can be the distance to
 * and obstacle:
 * <ul>
 * <li>positive when outside the obstacle,
 * <li>zero when in contact with the obstacle, and
 * <li>negative when in collision
 * </ul> */
public abstract class ImplicitFunctionRegion<T> implements Region<T>, SignedDistanceFunction<T> {
  @Override // from Region<Tensor>
  public final boolean isMember(T element) {
    return Sign.isNegativeOrZero(signedDistance(element));
  }
}
