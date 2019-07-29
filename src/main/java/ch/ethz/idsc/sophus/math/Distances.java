// code by jph
package ch.ethz.idsc.sophus.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Differences;

/** implementation taken from {@link Differences} */
public enum Distances {
  ;
  /** @param tensorMetric
   * @param tensor
   * @return */
  public static Tensor of(TensorMetric tensorMetric, Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length <= 1) {
      Objects.requireNonNull(tensorMetric);
      return Tensors.empty();
    }
    List<Tensor> list = new ArrayList<>(length - 1);
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor prev = iterator.next();
    for (int index = 1; index < length; ++index) {
      Tensor next = iterator.next();
      list.add(tensorMetric.distance(prev, next));
      prev = next;
    }
    return Unprotect.using(list);
  }
}
