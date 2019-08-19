// code by mh, jph
package ch.ethz.idsc.sophus.flt.ga;

import java.util.Iterator;

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
    // TODO JPH scalar!
    if (tensor.length() < 2)
      return tensor.copy();
    Tensor center = Tensors.reserve(tensor.length());
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor prev = iterator.next();
    Tensor curr = iterator.next();
    center.append(prev);
    while (iterator.hasNext()) {
      Tensor next = iterator.next();
      center.append(average(prev, curr, next));
      prev = curr;
      curr = next;
    }
    return center.append(curr);
  }
}
