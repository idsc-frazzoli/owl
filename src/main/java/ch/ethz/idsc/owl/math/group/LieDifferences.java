// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** generalization of {@link Differences} */
public class LieDifferences implements TensorUnaryOperator {
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;

  public LieDifferences(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieGroup = lieGroup;
    this.lieExponential = lieExponential;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Tensors.empty();
    Tensor prev = tensor.get(0);
    for (int index = 1; index < tensor.length(); ++index) {
      Tensor next = tensor.get(index);
      Tensor delta = lieGroup.element(prev).inverse().combine(next);
      result.append(lieExponential.log(delta));
      prev = next;
    }
    return result;
  }
}
