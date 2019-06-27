// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSamplesWrap;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Sign;

/** suggested base class for all implementations of {@link Transition} */
public abstract class AbstractTransition implements Transition, Serializable {
  private final Tensor start;
  private final Tensor end;
  private final Scalar length;

  public AbstractTransition(TransitionSpace transitionSpace, Tensor start, Tensor end) {
    this.start = start.unmodifiable();
    this.end = end.unmodifiable();
    length = transitionSpace.distance(this);
  }

  @Override // from Transition
  public final Scalar length() {
    return length;
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
  public Tensor sampled(Scalar minResolution) {
    if (Sign.isNegative(minResolution))
      throw TensorRuntimeException.of(minResolution);
    return sampled((int) Math.ceil(length.divide(minResolution).number().doubleValue()));
  }
}
