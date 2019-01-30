// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.VectorTotal;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class CenterWindowSampler implements IntegerTensorFunction {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(VectorTotal.FUNCTION);
  // ---
  private final ScalarUnaryOperator windowFunction;
  private final boolean isContinuous;

  /** @param windowFunction for evaluation in the interval [-1/2, +1/2] */
  public CenterWindowSampler(ScalarUnaryOperator windowFunction) {
    this.windowFunction = windowFunction;
    isContinuous = Chop._03.allZero(windowFunction.apply(RationalScalar.HALF));
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
