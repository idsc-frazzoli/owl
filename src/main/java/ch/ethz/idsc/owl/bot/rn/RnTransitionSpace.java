// code by jph, gjoel
package ch.ethz.idsc.owl.bot.rn;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Norm;

public class RnTransitionSpace implements TransitionSpace, Serializable {
  public static final TransitionSpace INSTANCE = new RnTransitionSpace();

  private RnTransitionSpace() {
    // ---
  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    Scalar length = distance(start, end);
    return new AbstractTransition(start, end, length) {
      @Override // from Transition
      public Tensor sampled(int steps) {
        if (steps < 1)
          throw TensorRuntimeException.of(length(), RealScalar.of(steps));
        Tensor samples = Array.zeros(steps);
        Tensor direction = end.subtract(start).divide(RealScalar.of(steps));
        samples.set(start, 0);
        if (steps > 1)
          IntStream.range(1, steps).parallel().forEach(i -> samples.set(direction.multiply(RealScalar.of(i)).add(start), i));
        return samples;
      }

      @Override // from Transition
      public TransitionWrap wrapped(int steps) {
        if (steps < 1)
          throw TensorRuntimeException.of(length(), RealScalar.of(steps));
        Scalar step = length.divide(RealScalar.of(steps));
        Tensor spacing = Array.zeros(steps);
        IntStream.range(0, steps).parallel().forEach(i -> spacing.set(i > 0 //
            ? step //
            : start.Get(0).zero(), i));
        return new TransitionWrap(sampled(steps), spacing);
      }
    };
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return Norm._2.between(start, end);
  }
}
