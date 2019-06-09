// code by jph
package ch.ethz.idsc.sophus.math;

import java.util.function.Function;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class WindowCenterSampler extends WindowBaseSampler {
  public static Function<Integer, Tensor> of(ScalarUnaryOperator windowFunction) {
    return MemoFunction.wrap(new WindowCenterSampler(windowFunction));
  }

  // ---
  /** @param windowFunction for evaluation in the interval [-1/2, +1/2] */
  private WindowCenterSampler(ScalarUnaryOperator windowFunction) {
    super(windowFunction);
  }

  @Override // from WindowBaseSampler
  protected Tensor samples(int extent) {
    return isContinuous //
        ? Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * extent + 2) //
            .map(windowFunction) //
            .extract(1, 2 * extent + 2)
        : Subdivide.of(RationalScalar.HALF.negate(), RationalScalar.HALF, 2 * extent) //
            .map(windowFunction);
  }
}
