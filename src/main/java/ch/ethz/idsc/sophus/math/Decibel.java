// code by ob, jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum Decibel implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final ScalarUnaryOperator LOG10 = Log.base(10);
  private static final Scalar _20 = RealScalar.of(20);

  @Override
  public Scalar apply(Scalar scalar) {
    return LOG10.apply(scalar).multiply(_20);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
