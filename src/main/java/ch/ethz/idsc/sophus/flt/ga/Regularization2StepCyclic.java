// code by mh, jph
package ch.ethz.idsc.sophus.flt.ga;

import java.util.Iterator;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** @see Regularization2Step */
/* package */ class Regularization2StepCyclic extends Regularization2Step {
  public Regularization2StepCyclic(SplitInterface splitInterface, Scalar factor) {
    super(splitInterface, factor);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int last = tensor.length() - 1;
    if (last < 1)
      return tensor.copy();
    Tensor center = Tensors.reserve(tensor.length());
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor prev = iterator.next();
    Tensor curr = iterator.next();
    center.append(average(tensor.get(last), prev, curr));
    while (iterator.hasNext()) {
      Tensor next = iterator.next();
      center.append(average(prev, curr, next));
      prev = curr;
      curr = next;
    }
    return center.append(average(prev, curr, tensor.get(0)));
  }
}
