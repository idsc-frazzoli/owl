// code by gjoel
package ch.ethz.idsc.owl.bot.rn;

import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.red.Norm;

public class RnTransition extends AbstractTransition {
  public RnTransition(Tensor start, Tensor end) {
    super(start, end, Norm._2.between(start, end));
  }

  @Override // from Transition
  public Tensor sampled(int steps) {
    if (steps < 1)
      throw TensorRuntimeException.of(length(), RealScalar.of(steps));
    if (steps == 0)
      return Tensors.of(start());
    // TODO JPH improve
    return Tensor.of(Subdivide.of(start(), end(), steps).stream().limit(steps));
  }

  @Override // from Transition
  public TransitionWrap wrapped(int steps) {
    if (steps < 1)
      throw TensorRuntimeException.of(length(), RealScalar.of(steps));
    Scalar step = length().divide(RealScalar.of(steps));
    Tensor spacing = Array.zeros(steps);
    IntStream.range(0, steps).forEach(i -> spacing.set(i > 0 //
        ? step //
        : start().Get(0).zero(), i));
    return new TransitionWrap(sampled(steps), spacing);
  }

  @Override // from RenderTransition
  public Tensor rendered(Scalar minResolution, int minSteps) {
    return Tensors.of(start(), end());
  }
}
