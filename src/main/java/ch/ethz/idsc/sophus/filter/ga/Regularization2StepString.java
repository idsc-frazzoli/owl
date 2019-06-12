// code by mh, jph
package ch.ethz.idsc.sophus.filter.ga;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** @see Regularization2Step */
/* package */ class Regularization2StepString extends Regularization2Step {
  public Regularization2StepString(SplitInterface splitInterface, Scalar factor) {
    super(splitInterface, factor);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor center = Tensors.empty();
    int last = tensor.length() - 1;
    if (last < 1)
      return tensor.copy();
    Tensor prev = tensor.get(0);
    Tensor curr = tensor.get(1);
    center.append(prev);
    for (int index = 1; index < last; ++index) {
      Tensor next = tensor.get(index + 1);
      center.append(average(prev, curr, next));
      prev = curr;
      curr = next;
    }
    center.append(curr);
    return center;
  }
}
