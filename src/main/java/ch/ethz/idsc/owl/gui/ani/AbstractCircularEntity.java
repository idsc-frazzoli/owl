// code by jph
package ch.ethz.idsc.owl.gui.ani;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.Tensor;

public abstract class AbstractCircularEntity extends AbstractEntity {
  public AbstractCircularEntity(TrajectoryControl trajectoryControl) {
    super(trajectoryControl);
  }

  @Override
  public final PlannerType getPlannerType() {
    return PlannerType.STANDARD;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    { // indicate current position
      Tensor state = getStateTimeNow().state();
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(64, 128, 64, 192));
      graphics.fill(new Ellipse2D.Double(point.getX() - 2, point.getY() - 2, 7, 7));
    }
    { // indicate position 1[s] into the future
      Tensor state = getEstimatedLocationAt(delayHint());
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(255, 128, 128 - 64, 128 + 64));
      graphics.fill(new Rectangle2D.Double(point.getX() - 2, point.getY() - 2, 5, 5));
    }
  }
}
