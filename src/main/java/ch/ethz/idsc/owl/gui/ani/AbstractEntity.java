// code by jph
package ch.ethz.idsc.owl.gui.ani;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.math.state.EntityControl;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** universal entity subject to
 * 1) trajectory based control, {@link TrajectoryEntity}
 * 2) manual control e.g. via joystick
 * 3) passive motion */
public abstract class AbstractEntity implements RenderInterface, AnimationInterface {
  private final EpisodeIntegrator episodeIntegrator;
  private final EntityControl entityControl;

  protected AbstractEntity(EpisodeIntegrator episodeIntegrator, EntityControl entityControl) {
    this.episodeIntegrator = episodeIntegrator;
    this.entityControl = entityControl;
  }

  @Override
  public final synchronized void integrate(Scalar now) {
    Tensor u = entityControl.control(getStateTimeNow(), now);
    episodeIntegrator.move(u, now);
  }

  public final StateTime getStateTimeNow() {
    return episodeIntegrator.tail();
  }
}
