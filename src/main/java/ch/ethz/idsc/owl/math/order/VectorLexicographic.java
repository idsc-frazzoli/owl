// code by jph
package ch.ethz.idsc.owl.math.order;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** lexicographic comparator for vectors
 * 
 * anti-symmetric bifunction: when the input parameters are swapped, the return value flips sign
 * 
 * returns -1 if vector1 < vector2 */
public enum VectorLexicographic implements Comparator<Tensor> {
  COMPARATOR;

  @Override // from Comparator
  public int compare(Tensor vector1, Tensor vector2) {
    // TODO checks can be tuned down
    VectorQ.requireLength(vector1, VectorQ.require(vector2).length());
    int cmp = 0;
    for (int index = 0; index < vector1.length() && cmp == 0; ++index)
      cmp = Scalars.compare(vector1.Get(index), vector2.Get(index));
    return cmp;
  }
}
