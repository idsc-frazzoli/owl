// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum BlackmanWindow implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar _21_50 = RationalScalar.of(21, 50);
  private static final Scalar _2_25 = RationalScalar.of(2, 25);
  private static final Scalar TWO_PI = RealScalar.of(2 * Math.PI);
  private static final Scalar _4_PI = RealScalar.of(4 * Math.PI);

  @Override
  public Scalar apply(Scalar x) {
    return _21_50 //
        .add(RationalScalar.HALF.multiply(Cos.FUNCTION.apply(x.multiply(TWO_PI)))) //
        .add(_2_25.multiply(Cos.FUNCTION.apply(x.multiply(_4_PI))));
  }
}
