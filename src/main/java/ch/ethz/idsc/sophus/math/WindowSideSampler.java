// code by ob
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class WindowSideSampler extends WindowBaseSampler {
  /** @param windowFunction for evaluation in the interval [-1/2, +1/2] */
  public WindowSideSampler(ScalarUnaryOperator windowFunction) {
    super(windowFunction);
  }

  @Override
  public Tensor apply(Integer i) {
    if (i == 0)
      return Tensors.vector(1);
    Tensor vector = isContinuous //
        ? Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * i + 2) //
            .map(windowFunction) //
            .extract(1, 2 * i + 2)
        : Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * i) //
            .map(windowFunction);
    // Take only half of the values
    vector = vector.extract(0, i + 1);
    return NORMALIZE.apply(vector);
  }
}
