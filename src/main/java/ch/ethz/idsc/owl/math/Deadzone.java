// code by edo
// adapted by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class Deadzone implements ScalarUnaryOperator {
  public static Deadzone of(Scalar min, Scalar max) {
    return new Deadzone(min, max);
  }

  public static Deadzone of(Number min, Number max) {
    return of(RealScalar.of(min), RealScalar.of(max));
  }

  // ---
  private final Clip clip;

  private Deadzone(Scalar min, Scalar max) {
    clip = Clip.function(min, max);
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar scalar) {
    return scalar.subtract(clip.apply(scalar));
  }
}
