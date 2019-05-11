// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** Implements the semiorder where the utility function is the identity mapping on the real numbers.
 * 
 * OrderComparison.STRICTLY_PRECEDES if x + slack less than y,
 * OrderComparison.STRICTLY_SUCCEDES if y + slack less than x, or
 * OrderComparison.INDIFFERENT if neither of above. */
public class ScalarSlackSemiorder implements OrderComparator<Scalar>, Serializable {
  private final Scalar slack;

  /** @param slack */
  public ScalarSlackSemiorder(Scalar slack) {
    this.slack = slack;
  }

  @Override // from OrderComparator
  public OrderComparison compare(Scalar x, Scalar y) {
    if (Scalars.lessThan(x.add(slack), y))
      return OrderComparison.STRICTLY_PRECEDES;
    if (Scalars.lessThan(y.add(slack), x))
      return OrderComparison.STRICTLY_SUCCEEDS;
    return OrderComparison.INDIFFERENT;
  }
}
