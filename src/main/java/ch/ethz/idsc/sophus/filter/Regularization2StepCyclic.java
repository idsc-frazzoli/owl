// code by mh, jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** @see Regularization2Step */
/* package */ class Regularization2StepCyclic extends Regularization2Step {
  public Regularization2StepCyclic(GeodesicInterface geodesicInterface, Scalar factor) {
    super(geodesicInterface, factor);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor center = Tensors.empty();
    int last = tensor.length() - 1;
    if (last < 1)
      return tensor.copy();
    Tensor prev = tensor.get(0);
    Tensor curr = tensor.get(1);
    center.append(average(tensor.get(last), prev, curr));
    for (int index = 1; index < last; ++index) {
      Tensor next = tensor.get(index + 1);
      center.append(average(prev, curr, next));
      prev = curr;
      curr = next;
    }
    center.append(average(prev, curr, tensor.get(0)));
    return center;
  }
}
