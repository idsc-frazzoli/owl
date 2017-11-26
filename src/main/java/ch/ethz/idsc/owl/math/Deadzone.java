// code by edo
// code adapted by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class Deadzone implements ScalarUnaryOperator {
  public static Deadzone of(Scalar min, Scalar max) {
    return new Deadzone(min, max);
  }

  public static Deadzone of(Number min, Number max) {
    return of(RealScalar.of(min), RealScalar.of(max));
  }

  // ---
  private final Scalar min;
  private final Scalar max;

  private Deadzone(Scalar min, Scalar max) {
    if (Scalars.lessThan(max, min))
      throw TensorRuntimeException.of(min, max);
    this.min = min;
    this.max = max;
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar scalar) {
    if (Scalars.lessThan(scalar, min))
      return scalar.subtract(min);
    if (Scalars.lessThan(max, scalar))
      return scalar.subtract(max);
    return RealScalar.ZERO;
  }
}
