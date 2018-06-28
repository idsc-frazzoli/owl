// code by ynager
package ch.ethz.idsc.owl.glc.std;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.sca.Sign;

/** Lexicographical comparator with slack for {@link VectorScalar}s
 * 
 * the bifunction is not anti-symmetric */
/* package */ class RelaxedLexicographic implements Serializable {
  private final Tensor slack;

  public RelaxedLexicographic(Tensor slack) {
    this.slack = VectorQ.require(slack);
  }

  /** @param newMerit
   * @param oldMerit
   * @return -1 if newMerit < oldMerit */
  // TODO implementation of numerics is not compatible/well-defined for use with Quantity
  public int quasiCompare(Tensor newMerit, Tensor oldMerit) {
    if (oldMerit.length() != newMerit.length() || oldMerit.length() != slack.length())
      throw TensorRuntimeException.of(oldMerit, newMerit, slack);
    int cmp = 0;
    for (int index = 0; index < oldMerit.length() && cmp == 0; ++index) {
      Scalar min = oldMerit.Get(index);
      if (Scalars.isZero(min))
        cmp = Scalars.compare(newMerit.Get(index), min);
      else {
        Scalar diffRatio = newMerit.Get(index).subtract(min).divide(min);
        cmp = Scalars.lessEquals(diffRatio.abs(), slack.Get(index)) //
            ? 0
            : Sign.FUNCTION.apply(diffRatio).number().intValue();
      }
    }
    return cmp;
  }
}
