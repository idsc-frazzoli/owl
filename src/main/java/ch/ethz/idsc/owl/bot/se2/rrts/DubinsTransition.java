// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.util.function.Predicate;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class DubinsTransition extends AbstractTransition {
  private final DubinsPath dubinsPath;

  public DubinsTransition(Tensor start, Tensor end, DubinsPath dubinsPath) {
    super(start, end, dubinsPath.length());
    this.dubinsPath = dubinsPath;
  }

  private int steps(Scalar minResolution) {
    return Ceiling.FUNCTION.apply(length().divide(Sign.requirePositive(minResolution))).number().intValue();
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    int n = steps(minResolution);
    return Tensor.of(Subdivide.of(0, 1, n).stream() //
        .skip(1) //
        .map(Scalar.class::cast) //
        .map(dubinsPath.unit(start())));
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    int steps = steps(minResolution);
    Scalar step = length().divide(RealScalar.of(steps));
    Tensor spacing = Tensors.vector(i -> step, steps);
    return new TransitionWrap(sampled(minResolution), spacing);
  }

  @Override // from RenderTransition
  public Tensor linearized(Scalar minResolution) {
    if (dubinsPath.type().containsStraight()) {
      int steps = steps(minResolution);
      Scalar step = dubinsPath.length().divide(RealScalar.of(steps));
      ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(start());
      Tensor interp = dubinsPath.segments();
      Predicate<Scalar> nonStraight = s -> Scalars.lessEquals(s, dubinsPath.segments().Get(0)) || //
          (Scalars.lessEquals(dubinsPath.segments().Get(1), s) && Scalars.lessEquals(s, dubinsPath.segments().Get(2)));
      IntStream.range(0, steps).mapToObj(i -> step.multiply(RealScalar.of(i))).filter(nonStraight).forEach(interp::append);
      return Tensor.of(interp.stream().map(Tensor::Get).sorted().map(scalarTensorFunction));
    }
    return Join.of(Tensors.of(start()), sampled(minResolution));
  }
}
