// code by ynager
package ch.ethz.idsc.owl.bot.util;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Sign;

/** Lexicographical comparator with slack for VectorScalars */
public class RelaxedLexicographic {
  public static RelaxedLexicographic of(Tensor slack) {
    return new RelaxedLexicographic(slack);
  }
  // ---

  private final Tensor slack;

  private RelaxedLexicographic(Tensor slack) {
    this.slack = slack;
  }

  public int compare(Tensor newCost, Tensor oldCost) {
    if (oldCost.length() != newCost.length() || oldCost.length() != slack.length())
      throw TensorRuntimeException.of(oldCost, newCost, slack);
    for (int index = 0; index < oldCost.length(); ++index) {
      int cmp;
      if (Scalars.isZero(oldCost.Get(index))) {
        cmp = Scalars.compare(newCost.Get(index), oldCost.Get(index));
      } else {
        Scalar diffRatio = (newCost.Get(index).subtract(oldCost.Get(index))).divide(oldCost.Get(index));
        cmp = Scalars.lessEquals(diffRatio.abs(), slack.Get(index)) //
            ? 0
            : Sign.of(diffRatio).number().intValue();
      }
      if (cmp != 0)
        return cmp;
    }
    return 0;
  }
}
