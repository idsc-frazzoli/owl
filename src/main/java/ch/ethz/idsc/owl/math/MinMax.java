// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Entrywise;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/MinMax.html">MinMax</a> */
public class MinMax {
  public static MinMax of(Tensor tensor) {
    return new MinMax(tensor);
  }

  // ---
  private final Tensor min;
  private final Tensor max;

  private MinMax(Tensor tensor) {
    min = tensor.stream().reduce(Entrywise.min()).get().unmodifiable();
    max = tensor.stream().reduce(Entrywise.max()).get().unmodifiable();
  }

  public Tensor min() {
    return min;
  }

  public Tensor max() {
    return max;
  }
}
