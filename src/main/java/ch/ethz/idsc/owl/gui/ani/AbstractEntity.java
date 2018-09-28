// code by jph
package ch.ethz.idsc.owl.gui.ani;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.math.state.EntityControl;
import ch.ethz.idsc.owl.math.state.EntityControlComparator;
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
  private final Set<EntityControl> entityControls = //
      new ConcurrentSkipListSet<>(EntityControlComparator.INSTANCE);

  protected AbstractEntity(EpisodeIntegrator episodeIntegrator) {
    this.episodeIntegrator = episodeIntegrator;
  }

  protected final void add(EntityControl entityControl) {
    entityControls.add(entityControl);
  }

  private Scalar last = null;

  @Override
  public final synchronized void integrate(Scalar now) {
    Scalar delta = Objects.nonNull(last) //
        ? now.subtract(last)
        : now.zero();
    Scalar _now = getStateTimeNow().time().add(delta);
    // System.out.println(_now + " " + entityControls.size());
    for (EntityControl entityControl : entityControls) {
      Optional<Tensor> u = entityControl.control(getStateTimeNow(), _now);
      if (u.isPresent()) {
        episodeIntegrator.move(u.get(), _now);
        last = now;
        return;
      }
    }
    // if (Objects.nonNull(last))
    // System.err.println("switch to passive");
    last = null;
    // System.err.println("control missing");
    // throw new RuntimeException("control missing");
  }

  public final StateTime getStateTimeNow() {
    return episodeIntegrator.tail();
  }
}
