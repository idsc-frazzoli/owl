// code by jph
package ch.ethz.idsc.owl.ani.api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;

// TODO JPH first API draft, unify with se2entity and abstract entity
public abstract class PolicyEntity implements AnimationInterface, RenderInterface {
  protected EpisodeIntegrator episodeIntegrator;
  public TrajectoryRegionQuery obstacleQuery = null;

  /** @param stateTime
   * @return state of discrete model */
  public abstract Tensor represent(StateTime stateTime);

  public final StateTime getStateTimeNow() {
    return episodeIntegrator.tail();
  }

  protected abstract Tensor shape();

  private boolean obstacleQuery_isDisjoint(StateTime stateTime) {
    return Objects.isNull(obstacleQuery) || !obstacleQuery.isMember(stateTime);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    { // indicate current position
      final StateTime stateTime = getStateTimeNow();
      geometricLayer.pushMatrix(Se2Matrix.of(stateTime.state()));
      graphics.fill(geometricLayer.toPath2D(shape()));
      geometricLayer.popMatrix();
    }
    { // draw mouse
      Color color = new Color(0, 128, 255, 192);
      Tensor xya = geometricLayer.getMouseSe2State();
      StateTime stateTime = new StateTime(xya, getStateTimeNow().time());
      if (!obstacleQuery_isDisjoint(stateTime))
        color = new Color(255, 96, 96, 128);
      geometricLayer.pushMatrix(Se2Matrix.of(xya));
      graphics.setColor(color);
      graphics.fill(geometricLayer.toPath2D(shape()));
      geometricLayer.popMatrix();
    }
  }
}
