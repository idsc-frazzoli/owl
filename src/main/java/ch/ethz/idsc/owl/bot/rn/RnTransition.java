// code by gjoel
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;

public class RnTransition extends AbstractTransition {
  public RnTransition(RrtsNode start, Tensor end) {
    super(start, end, Norm._2.between(start.state(), end));
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    return Tensor.of(Subdivide.of(start().state(), end(), steps(minResolution)).stream().skip(1));
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    int steps = steps(minResolution);
    Scalar resolution = length().divide(RealScalar.of(steps));
    Tensor spacing = Tensors.vector(i -> resolution, steps);
    return new TransitionWrap(sampled(minResolution), spacing);
  }

  @Override // from RenderTransition
  public Tensor linearized(Scalar minResolution) {
    return Tensors.of(start().state(), end());
  }

  private int steps(Scalar minResolution) {
    return Ceiling.FUNCTION.apply(length().divide(Sign.requirePositive(minResolution))).number().intValue();
  }
}
