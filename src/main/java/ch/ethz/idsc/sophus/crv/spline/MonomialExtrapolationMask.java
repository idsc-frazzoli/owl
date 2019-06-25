// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.sophus.util.MemoFunction;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Binomial;

/** the weights follow from a linear system of equations with entries
 * LHS=x^k for x=-deg, ..., 0, and k = 0, ..., deg
 * RHS=1^k for k = 0, ..., deg
 * 
 * For instance, deg=3
 * LHS=
 * +1 +1 +1 +1 : x^0
 * -3 -2 -1 +0 : x^1
 * +9 +4 +1 +0 : x^2
 * -27 -8 -1 0 : x^3
 * 
 * RHS=[1 1 1 1]' */
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
