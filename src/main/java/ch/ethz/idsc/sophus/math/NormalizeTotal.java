// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;

/** Consistent with Mathematica, in particular
 * Mathematica::Normalize[{}, Total] == {} */
public enum NormalizeTotal implements TensorUnaryOperator {
  FUNCTION;
  // ---
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);

  @Override
  public Tensor apply(Tensor vector) {
    return NORMALIZE.apply(vector);
  }
}
