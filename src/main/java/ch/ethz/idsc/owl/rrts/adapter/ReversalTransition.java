// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;

public abstract class ReversalTransition extends DirectedTransition {
  public ReversalTransition(Transition transition) {
    super(transition, false);
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    return swap(transition.sampled(minResolution));
  }

  @Override // from Transition
  public Tensor sampled(int steps) {
    return swap(transition.sampled(steps));
  }

  private Tensor swap(Tensor samples) {
    return Reverse.of(samples.extract(1, samples.length()).append(start()));
  }
}
