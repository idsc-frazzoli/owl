// code by ynager
package ch.ethz.idsc.owl.math.order;

import java.io.Serializable;
import java.util.Comparator;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Floor;

/** comparator for vectors
 * 
 * anti-symmetric bifunction: when the input parameters are swapped, the return value flips sign
 * 
 * returns -1 if t1 < t2 */
public class DiscretizedLexicographic implements Comparator<Tensor>, Serializable {
  /** @param slack
   * @return */
  public static final DiscretizedLexicographic of(Tensor slack) {
    return new DiscretizedLexicographic(slack);
  }

  /***************************************************/
  private final Tensor slack;

  private DiscretizedLexicographic(Tensor slack) {
    this.slack = slack;
    // any conditions regarding entries on slack: non-negativity?
  }

  @Override // from Comparator
  public int compare(Tensor t1, Tensor t2) {
    if (t1.length() != t2.length())
      throw TensorRuntimeException.of(t1, t2);
    int cmp = 0;
    for (int index = 0; index < t1.length() && cmp == 0; ++index) {
      Scalar a = t1.Get(index);
      Scalar b = t2.Get(index);
      Scalar s = slack.Get(index);
      if (Scalars.nonZero(s)) {
        // multiplication with s should be obsolete for comparison
        a = Floor.FUNCTION.apply(a.divide(s)); // .multiply(s)
        b = Floor.FUNCTION.apply(b.divide(s)); // .multiply(s)
      }
      cmp = Scalars.compare(a, b);
    }
    // suggestion by jan is to simply return 0 here
    if (cmp == 0)
      cmp = VectorLexicographic.COMPARATOR.compare(t1, t2);
    return cmp;
  }
}
