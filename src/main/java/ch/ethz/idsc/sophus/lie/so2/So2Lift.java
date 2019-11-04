// code by jph
package ch.ethz.idsc.sophus.lie.so2;

import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class So2Lift implements ScalarUnaryOperator {
  /** @param vector
   * @return */
  public static Tensor of(Tensor vector) {
    return Tensor.of(vector.stream().map(Scalar.class::cast).map(new So2Lift()));
  }

  // ---
  private Scalar prev = null;

  @Override
  public Scalar apply(Scalar angle) {
    if (Objects.isNull(prev))
      return prev = angle;
    Scalar delta = So2.MOD.apply(angle.subtract(prev));
    return prev = prev.add(delta);
  }
}
