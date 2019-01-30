// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.VectorTotal;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public abstract class WindowBaseSampler implements IntegerTensorFunction {
  protected static final TensorUnaryOperator NORMALIZE = Normalize.with(VectorTotal.FUNCTION);
  // ---
  protected final ScalarUnaryOperator windowFunction;
  protected final boolean isContinuous;

  /** @param windowFunction for evaluation in the interval [-1/2, +1/2] */
  public WindowBaseSampler(ScalarUnaryOperator windowFunction) {
    this.windowFunction = windowFunction;
    isContinuous = Chop._03.allZero(windowFunction.apply(RationalScalar.HALF));
  }
}
