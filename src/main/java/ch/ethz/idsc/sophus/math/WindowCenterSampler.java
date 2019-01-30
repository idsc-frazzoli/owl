// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class WindowCenterSampler extends WindowBaseSampler {
  /** @param windowFunction for evaluation in the interval [-1/2, +1/2] */
  public WindowCenterSampler(ScalarUnaryOperator windowFunction) {
    super(windowFunction);
  }

  @Override // from IntegerTensorFunction
  public Tensor apply(Integer extent) {
    if (extent == 0)
      return Tensors.vector(1);
    Tensor vector = isContinuous //
        ? Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * extent + 2) //
            .map(windowFunction) //
            .extract(1, 2 * extent + 2)
        : Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * extent) //
            .map(windowFunction);
    return NORMALIZE.apply(vector);
  }
}
