// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.sophus.util.MemoFunction;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Binomial;

public class MonomialExtrapolationMask implements Function<Integer, Tensor>, Serializable {
  public static final Function<Integer, Tensor> INSTANCE = MemoFunction.wrap(new MonomialExtrapolationMask());

  // ---
  private MonomialExtrapolationMask() {
    // ---
  }

  @Override
  public Tensor apply(Integer length) {
    Binomial binomial = Binomial.of(length);
    int negate = length % 2;
    return Tensors.vector(k -> k % 2 == negate //
        ? binomial.over(k).negate()
        : binomial.over(k), length);
  }
}
