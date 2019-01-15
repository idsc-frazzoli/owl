// code by jph, yn
package ch.ethz.idsc.owl.glc.rl;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

public class SlackWrap {
  private final Tensor slack;

  public SlackWrap(Tensor slack) {
    this.slack = slack;
  }

  // TODO YN name is not good, more like: "isBetter" ?
  public boolean isWithin(Tensor merit, Tensor entrywiseMin) {
    Tensor diff = entrywiseMin.add(slack).subtract(merit);
    // old: diff.stream().map(Scalar.class::cast).allMatch(Sign::isPositiveOrZero);
    return !diff.stream().map(Scalar.class::cast).anyMatch(Sign::isNegative);
  }
}
