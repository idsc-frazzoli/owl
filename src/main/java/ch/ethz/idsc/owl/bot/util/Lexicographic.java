// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;

/** comparator for vectors
 * 
 * anti-symmetric bifunction: when the input parameters are swapped, the return value flips sign
 * 
 * returns -1 if t1 < t2 */
public enum Lexicographic implements Comparator<Tensor> {
  COMPARATOR;
  // ---
  @Override // from Comparator
  public int compare(Tensor t1, Tensor t2) {
    if (t1.length() != t2.length())
      throw TensorRuntimeException.of(t1, t2);
    int cmp = 0;
    for (int index = 0; index < t1.length() && cmp == 0; ++index)
      cmp = Scalars.compare(t1.Get(index), t2.Get(index));
    return cmp;
  }
}
