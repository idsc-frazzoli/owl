// code by jph, gjoel
package ch.ethz.idsc.owl.bot.rn;

import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.adapter.AbstractTransitionSpace;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Norm;

public class RnTransitionSpace extends AbstractTransitionSpace {
  public static final TransitionSpace INSTANCE = new RnTransitionSpace();

  private RnTransitionSpace() {
    // ---
  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    return new AbstractTransition(this, start, end) {
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
    };
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return Norm._2.between(start, end);
  }
}
