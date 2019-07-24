// code by jph
package ch.ethz.idsc.sophus.lie;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** LieDifferences is the generalization of {@link Differences}
 * The input are elements from the Lie group.
 * The return sequence consists of elements from the Lie algebra.
 * 
 * <pre>
 * LieDifferences[{a, b, c, d, e}] == {log a^-1.b, log b^-1.c, log c^-1.d, log d^-1.e}
 * </pre> */
public final class LieDifferences implements TensorUnaryOperator {
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;

  /** @param lieGroup
   * @param lieExponential
   * @throws Exception if either parameter is null */
  public LieDifferences(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieGroup = Objects.requireNonNull(lieGroup);
    this.lieExponential = Objects.requireNonNull(lieExponential);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Unprotect.empty(tensor.length() - 1);
    Tensor prev = tensor.get(0);
    for (int index = 1; index < tensor.length(); ++index)
      result.append(pair(prev, prev = tensor.get(index)));
    return result;
  }

  /** @param p element of the lie group
   * @param q element of the lie group
   * @return vector == log(p^-1 . q) so that exp(vector) == p^-1 . q */
  public Tensor pair(Tensor p, Tensor q) {
    return lieExponential.log(lieGroup.element(p).inverse().combine(q));
  }
}
