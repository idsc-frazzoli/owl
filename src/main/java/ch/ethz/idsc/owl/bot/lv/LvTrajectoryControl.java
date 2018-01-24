// code by jph
package ch.ethz.idsc.owl.bot.lv;

import ch.ethz.idsc.owl.math.state.AbstractTrajectoryControl;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm2Squared;

public class LvTrajectoryControl extends AbstractTrajectoryControl {
  
  public static final Tensor FALLBACK_CONTROL = Tensors.vectorDouble(0).unmodifiable();

  public LvTrajectoryControl(EpisodeIntegrator episodeIntegrator) {
    super(episodeIntegrator, StateTime::state);
  }

  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    return Norm2Squared.between(x, y);
  }

  @Override
  protected Tensor fallbackControl() {
    return FALLBACK_CONTROL;
  }
}