// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;

public abstract class ReversalTransition extends DirectedTransition {
  /* package */ ReversalTransition(Transition transition) {
    super(transition, false);
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    return swap(transition.sampled(minResolution));
  }

  private Tensor swap(Tensor samples) {
    // return Reverse.of(samples.extract(1, samples.length()).append(start()));
    return Reverse.of(samples.extract(0, samples.length() - 1)).append(end());
  }
}
