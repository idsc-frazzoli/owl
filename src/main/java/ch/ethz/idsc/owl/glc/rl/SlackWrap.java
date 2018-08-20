// code by jph, yn
package ch.ethz.idsc.owl.glc.rl;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

public class SlackWrap {
  private final Tensor slack;

  public SlackWrap(Tensor slack) {
    this.slack = slack;
  }

  public boolean isWithin(Tensor merit, Tensor entrywiseMin) {
    Tensor diff = entrywiseMin.add(slack).subtract(merit);
    // TODO diff.stream().map(Tensor::Get).allMatch(Sign::isPositiveOrZero);
    return !diff.stream().map(Tensor::Get).anyMatch(Sign::isNegative);
  }
}
