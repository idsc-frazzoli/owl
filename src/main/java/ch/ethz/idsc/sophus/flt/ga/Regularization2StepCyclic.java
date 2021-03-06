// code by mh, jph
package ch.ethz.idsc.sophus.flt.ga;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Last;

/** @see Regularization2Step */
/* package */ class Regularization2StepCyclic extends Regularization2Step {
  public Regularization2StepCyclic(SplitInterface splitInterface, Scalar factor) {
    super(splitInterface, factor);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    if (tensor.length() < 2) {
      ScalarQ.thenThrow(tensor);
      return tensor.copy();
    }
    List<Tensor> list = new ArrayList<>(tensor.length());
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor prev = iterator.next();
    Tensor curr = iterator.next();
    list.add(average(Last.of(tensor), prev, curr));
    while (iterator.hasNext()) {
      Tensor next = iterator.next();
      list.add(average(prev, curr, next));
      prev = curr;
      curr = next;
    }
    list.add(average(prev, curr, tensor.get(0)));
    return Unprotect.using(list);
  }
}
