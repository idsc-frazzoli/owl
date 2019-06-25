// code by ob
package ch.ethz.idsc.sophus.math.win;

import java.util.function.Function;

import ch.ethz.idsc.sophus.util.MemoFunction;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** samples a given window function uniformly in the interval [-1/2, 0] */
public class HalfWindowSampler extends WindowBaseSampler {
  /** @param windowFunction for evaluation in the interval [-1/2, +1/2] */
  public static Function<Integer, Tensor> of(ScalarUnaryOperator windowFunction) {
    return MemoFunction.wrap(new HalfWindowSampler(windowFunction));
  }

  // ---
  private HalfWindowSampler(ScalarUnaryOperator windowFunction) {
    super(windowFunction);
  }

  @Override // from WindowBaseSampler
  protected Tensor samples(int length) {
    return isContinuous //
        ? Subdivide.of(RationalScalar.HALF.negate(), RealScalar.ZERO, length) //
            .map(windowFunction) //
            .extract(1, length + 1)
        : Subdivide.of(RationalScalar.HALF.negate(), RealScalar.ZERO, length - 1) //
            .map(windowFunction);
  }
}
