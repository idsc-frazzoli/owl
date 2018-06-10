// code by ynager
package ch.ethz.idsc.owl.glc.std;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Sign;

/** Lexicographical comparator with slack for {@link VectorScalar}s
 * 
 * the bifunction is not anti-symmetric */
/* package */ class RelaxedLexicographic implements Serializable {
  private final Tensor slack;

  public RelaxedLexicographic(Tensor slack) {
    this.slack = slack;
  }

  // TODO function name causes confusion with Comparable interface
  public int compare(Tensor newCost, Tensor oldCost) {
    if (oldCost.length() != newCost.length() || oldCost.length() != slack.length())
      throw TensorRuntimeException.of(oldCost, newCost, slack);
    int cmp = 0;
    for (int index = 0; index < oldCost.length() && cmp == 0; ++index) {
      Scalar min = oldCost.Get(index);
      if (Scalars.isZero(min))
        cmp = Scalars.compare(newCost.Get(index), min);
      else {
        Scalar diffRatio = newCost.Get(index).subtract(min).divide(min);
        cmp = Scalars.lessEquals(diffRatio.abs(), slack.Get(index)) //
            ? 0
            : Sign.of(diffRatio).number().intValue();
      }
    }
    return cmp;
  }
}
