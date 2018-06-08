// code by ynager
package ch.ethz.idsc.owl.bot.util;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Sign;

/** Lexicographical comparator with slack for VectorScalars */
public class RelaxedLexicographic implements Serializable {
  private final Tensor slack;

  public RelaxedLexicographic(Tensor slack) {
    this.slack = slack;
  }

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
