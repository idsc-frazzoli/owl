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
    min = tensor.stream().reduce(Entrywise.min()).get(); // {-0.295, -0.725, -0.25}
    max = tensor.stream().reduce(Entrywise.max()).get(); // {1.765, 0.725, -0.25}
  }

  public Tensor min() {
    return min.unmodifiable();
  }

  public Tensor max() {
    return max.unmodifiable();
  }
}
