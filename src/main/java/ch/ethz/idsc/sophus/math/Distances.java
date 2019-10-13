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
   * @param tensor of length n
   * @return vector of length n - 1, or the empty vector if n == 0
   * @throws Exception if given tensor is a scalar */
  public static Tensor of(TensorMetric tensorMetric, Tensor tensor) {
    int length = tensor.length();
    if (length <= 1) {
      Objects.requireNonNull(tensorMetric);
      ScalarQ.thenThrow(tensor);
      return Tensors.empty();
    }
    List<Tensor> list = new ArrayList<>(length - 1);
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor prev = iterator.next();
    for (int index = 1; index < length; ++index)
      list.add(tensorMetric.distance(prev, prev = iterator.next()));
    return Unprotect.using(list);
  }
}
