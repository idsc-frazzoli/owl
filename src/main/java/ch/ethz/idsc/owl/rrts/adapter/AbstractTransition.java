// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;

/** suggested base class for all implementations of {@link Transition} */
public abstract class AbstractTransition implements Transition, Serializable {
  private final Tensor start;
  private final Tensor end;
  private final Scalar length;

  public AbstractTransition(Tensor start, Tensor end, Scalar length) {
    this.start = start.unmodifiable();
    this.end = end.unmodifiable();
    this.length = length;
  }

  @Override // from Transition
  public final Tensor start() {
    return start;
  }

  @Override // from Transition
  public final Tensor end() {
    return end;
  }

  @Override // from Transition
  public final Scalar length() {
    return length;
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    return sampled(Ceiling.FUNCTION.apply(length.divide(minResolution)).number().intValue());
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    return wrapped(Ceiling.FUNCTION.apply(length.divide(minResolution)).number().intValue());
  }
}
