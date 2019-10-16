// code by jph
package ch.ethz.idsc.owl.math;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Entrywise;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/MinMax.html">MinMax</a> */
// TODO JPH OWL 057 move to ch.ethz.idsc.sophus.math
public class MinMax implements Serializable {
  /** @param tensor not a scalar
   * @return */
  public static MinMax of(Tensor tensor) {
    return new MinMax(tensor);
  }

  // ---
  private final Tensor min;
  private final Tensor max;

  private MinMax(Tensor tensor) {
    min = Entrywise.min().of(tensor);
    max = Entrywise.max().of(tensor);
  }

  public Tensor min() {
    return min.unmodifiable();
  }

  public Tensor max() {
    return max.unmodifiable();
  }
}
