// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class DubinsTransition extends AbstractTransition {
  private final DubinsPath dubinsPath;

  public DubinsTransition(Tensor start, Tensor end, DubinsPath dubinsPath) {
    super(start, end, dubinsPath.length());
    this.dubinsPath = dubinsPath;
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    int steps = Ceiling.FUNCTION.apply(length().divide(minResolution)).number().intValue();
    if (steps < 1)
      throw TensorRuntimeException.of(RealScalar.of(steps));
    Tensor samples = Array.zeros(steps);
    Scalar step = dubinsPath.length().divide(RealScalar.of(steps));
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(start());
    IntStream.range(0, steps).forEach(i -> samples.set(scalarTensorFunction.apply(step.multiply(RealScalar.of(i + 1))), i));
    return samples;
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    int steps = Ceiling.FUNCTION.apply(length().divide(minResolution)).number().intValue();
    if (steps < 1)
      throw TensorRuntimeException.of(RealScalar.of(steps));
    Tensor samples = Array.zeros(steps);
    Scalar step = dubinsPath.length().divide(RealScalar.of(steps));
    ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(start());
    IntStream.range(0, steps).forEach(i -> samples.set(scalarTensorFunction.apply(step.multiply(RealScalar.of(i + 1))), i));
    Tensor spacing = Tensors.vector(i -> step, steps);
    return new TransitionWrap(samples, spacing);
  }

  @Override // from RenderTransition
  public Tensor linearized(Scalar minResolution) {
    // TODO GJOEL/JPH straight section could be simplified
    return Join.of(Tensors.of(start()), sampled(minResolution));
  }
}
