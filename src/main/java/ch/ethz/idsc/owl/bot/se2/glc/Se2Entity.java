// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.gui.ani.AbstractEntity;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.N;

/** several magic constants are hard-coded in the implementation.
 * that means, the functionality does not apply to all examples universally. */
public abstract class Se2Entity extends AbstractEntity {
  private static final Tensor FALLBACK_CONTROL = N.DOUBLE.of(Array.zeros(3)).unmodifiable();
  // ---
  public final Collection<CostFunction> extraCosts = new LinkedList<>();
  public TrajectoryRegionQuery obstacleQuery = null;

  protected Se2Entity(EpisodeIntegrator episodeIntegrator) {
    super(episodeIntegrator);
  }

  @Override
  protected final Tensor fallbackControl() {
    return FALLBACK_CONTROL;
  }

  private boolean obstacleQuery_isDisjoint(StateTime stateTime) {
    return Objects.isNull(obstacleQuery) || !obstacleQuery.isMember(stateTime);
  }

  protected abstract Tensor eta();

  protected abstract Tensor shape();

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    { // indicate current position
      final StateTime stateTime = getStateTimeNow();
      Color color = obstacleQuery_isDisjoint(stateTime) //
          ? new Color(64, 64, 64, 128)
          : new Color(255, 64, 64, 128);
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(stateTime.state()));
      graphics.setColor(color);
      graphics.fill(geometricLayer.toPath2D(shape()));
      geometricLayer.popMatrix();
    }
    { // indicate position delay[s] into the future
      Tensor state = getEstimatedLocationAt(delayHint());
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(255, 128, 64, 192));
      graphics.fill(new Rectangle2D.Double(point.getX() - 2, point.getY() - 2, 5, 5));
    }
    { // draw mouse
      Color color = new Color(0, 128, 255, 192);
      StateTime stateTime = new StateTime(geometricLayer.getMouseSe2State(), getStateTimeNow().time());
      if (!obstacleQuery_isDisjoint(stateTime))
        color = new Color(255, 96, 96, 128);
      geometricLayer.pushMatrix(geometricLayer.getMouseSe2Matrix());
      graphics.setColor(color);
      graphics.fill(geometricLayer.toPath2D(shape()));
      geometricLayer.popMatrix();
    }
  }
}
