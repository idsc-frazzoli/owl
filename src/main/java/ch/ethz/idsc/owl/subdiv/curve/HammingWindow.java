// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** TODO at the moment does not cutoff outside [-1/2, 1/2] */
// TODO V062
public enum HammingWindow implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar _25_46 = RationalScalar.of(25, 46);
  private static final Scalar _21_46 = RationalScalar.of(21, 46);
  private static final Scalar TWO_PI = RealScalar.of(2 * Math.PI);

  @Override
  public Scalar apply(Scalar x) {
    return _25_46.add(_21_46.multiply(Cos.FUNCTION.apply(x.multiply(TWO_PI))));
  }
}
