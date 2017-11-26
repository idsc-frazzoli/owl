// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;

/** returns -1 if t1 < t2 */
public enum Lexicographic implements Comparator<Tensor> {
  COMPARATOR;
  // ---
  @Override
  public int compare(Tensor t1, Tensor t2) {
    if (t1.length() != t2.length())
      throw TensorRuntimeException.of(t1, t2);
    for (int index = 0; index < t1.length(); ++index) {
      int cmp = Scalars.compare(t1.Get(index), t2.Get(index));
      if (cmp != 0)
        return cmp;
    }
    return 0;
  }
}
