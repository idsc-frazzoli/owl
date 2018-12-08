// code by jph
package ch.ethz.idsc.owl.math.dubins;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ enum StaticHelper {
  ;
  private static final ScalarUnaryOperator MOD_TWO_PI = Mod.function(RealScalar.of(2 * Math.PI));

  public static double principalValue(double angle) {
    return MOD_TWO_PI.apply(RealScalar.of(angle)).number().doubleValue();
  }
}
