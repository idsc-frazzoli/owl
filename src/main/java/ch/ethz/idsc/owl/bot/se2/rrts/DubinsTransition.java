// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/* package */ class DubinsTransition extends AbstractTransition {
  final DubinsPath dubinsPath;

  public DubinsTransition(Tensor start, Tensor end, DubinsPath dubinsPath) {
    super(start, end, dubinsPath.length()); // TODO GJOEL confirm assumption that length() == Euclidean length
    this.dubinsPath = dubinsPath;
  }

  @Override // from Transition
  public Tensor sampled(int steps) {
    if (steps < 1)
      throw TensorRuntimeException.of(RealScalar.of(steps));
    Tensor samples = Array.zeros(steps);
    Scalar step = dubinsPath.length().divide(RealScalar.of(steps));
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(start());
    IntStream.range(0, steps).forEach(i -> samples.set(scalarTensorFunction.apply(step.multiply(RealScalar.of(i))), i));
    return samples;
  }

  @Override // from Transition
  public TransitionWrap wrapped(int steps) {
    if (steps < 1)
      throw TensorRuntimeException.of(RealScalar.of(steps));
    Tensor samples = Array.zeros(steps);
    Tensor spacing = Array.zeros(steps);
    Scalar step = dubinsPath.length().divide(RealScalar.of(steps));
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(start());
    IntStream.range(0, steps).forEach(i -> {
      samples.set(scalarTensorFunction.apply(step.multiply(RealScalar.of(i))), i);
      spacing.set(i > 0 ? step : step.zero(), i);
    });
    return new TransitionWrap(samples, spacing);
  }

  @Override // from RenderTransition
  public Tensor linearized(Scalar minResolution, int minSteps) {
    return (Scalars.lessThan(minResolution, length().divide(RealScalar.of(minSteps))) //
        ? sampled(minResolution).copy() //
        : sampled(minSteps).copy()).append(end());
  }
}
