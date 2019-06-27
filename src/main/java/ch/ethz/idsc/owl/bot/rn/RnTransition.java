// code by jph, gjoel
package ch.ethz.idsc.owl.bot.rn;

import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.TransitionSamplesWrap;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Norm;

/** Transition as straight line connecting given start and end point in Euclidean space.
 * The length of the transition is Euclidean distance between given start and end point. */
// TODO GJOEL/JPH remove class if not needed anymore
// @Deprecated
/* package */ class RnTransition extends AbstractTransition {
  public RnTransition(Tensor start, Tensor end) {
    super(RnTransitionSpace.INSTANCE, start, end);
  }

  /*
  @Override // from Transition
  public Tensor sampled(Scalar ofs, Scalar dt) {
    // RnGeodesic.INSTANCE.curve(start(), end());
    // TODO JPH implementation not efficient
    if (Scalars.lessThan(dt, ofs))
      throw TensorRuntimeException.of(ofs, dt);
    Scalar length = RnTransitionSpace.INSTANCE.distance(this);
    Tensor tensor = Tensors.empty();
    while (Scalars.lessThan(ofs, length)) {
      Tensor x = start().multiply(length.subtract(ofs).divide(length)) //
          .add(end().multiply(ofs.divide(length)));
      tensor.append(x);
      ofs = ofs.add(dt);
    }
    return tensor;
  }
  */

  @Override // from Transition
  public TransitionSamplesWrap sampled(int steps) {
    if (steps < 1)
      throw TensorRuntimeException.of(RealScalar.of(steps));
    Tensor samples = Array.zeros(steps);
    Tensor spacing = Array.zeros(steps);
    Tensor direction = end().subtract(start()).divide(RealScalar.of(steps));
    Scalar step = Norm._2.ofVector(direction);
    samples.set(start(), 0);
    if (steps > 1)
      IntStream.range(1, steps).parallel().forEach(i -> {
        samples.set(direction.multiply(RealScalar.of(i)).add(start()), i);
        spacing.set(step, i);
      });
    return new TransitionSamplesWrap(samples, spacing);
  }
}
