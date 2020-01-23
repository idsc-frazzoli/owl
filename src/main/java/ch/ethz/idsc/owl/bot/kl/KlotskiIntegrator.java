package ch.ethz.idsc.owl.bot.kl;

import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

enum KlotskiIntegrator implements StateIntegrator {
  INSTANCE;
  @Override
  public List<StateTime> trajectory(StateTime stateTime, Flow flow) {
    Tensor xn = KlotskiModel.INSTANCE.f(stateTime.state(), flow.getU());
    return Collections.singletonList(new StateTime(xn, stateTime.time().add(RealScalar.ONE)));
  }
}
