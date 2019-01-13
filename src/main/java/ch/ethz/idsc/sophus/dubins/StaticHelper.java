// code by jph
package ch.ethz.idsc.sophus.dubins;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ enum StaticHelper {
  ;
  private static final ScalarUnaryOperator MOD_TWO_PI = Mod.function(Pi.TWO);

  public static Scalar principalValue(Scalar angle) {
    return MOD_TWO_PI.apply(angle);
  }

  // ---
  private static final Scalar NUMERIC_ONE = DoubleScalar.of(1);

  /** @param scalar
   * @return true if given scalar is greater than 1 */
  public static boolean greaterThanOne(Scalar scalar) {
    return Scalars.lessThan(NUMERIC_ONE, scalar);
  }
}
