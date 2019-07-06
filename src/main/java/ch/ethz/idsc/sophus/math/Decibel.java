// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum Decibel {
  ;
  private static final ScalarUnaryOperator LOG10 = Log.base(10);
  private static final Scalar _20 = RealScalar.of(20);

  public static Tensor of(Tensor magnitude) {
    return magnitude.map(LOG10).multiply(_20);
  }
}
