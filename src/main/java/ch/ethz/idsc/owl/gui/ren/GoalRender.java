// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.StateTimeCollector;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.sophus.ply.d2.ConvexHull;
import ch.ethz.idsc.tensor.Tensor;

public class GoalRender implements RenderInterface {
  public static final boolean CONVEX = true;
  public static final Color COLOR = new Color(224, 168, 0, 128);
  // ---
  private Collection<StateTime> collection;

  public GoalRender(Collection<StateTime> collection) {
    this.collection = collection;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.isNull(collection))
      return;
    if (CONVEX) { // draw convex hull of goal points
      Tensor points = Tensor.of(collection.stream().map(StateTime::state).map(Extract2D.FUNCTION));
      if (2 < points.length()) {
        graphics.setColor(COLOR);
        Path2D path2D = geometricLayer.toPath2D(ConvexHull.of(points));
        path2D.closePath();
        graphics.draw(path2D);
      }
    }
    { // draw discovered points
      double radius = 9;
      double offset = -radius * 0.5;
      graphics.setColor(COLOR);
      for (StateTime stateTime : collection) {
        Point2D point2d = geometricLayer.toPoint2D(stateTime.state());
        graphics.draw(new Ellipse2D.Double(point2d.getX() + offset, point2d.getY() + offset, radius, radius));
      }
    }
  }

  public void fromStateTimeCollector(Object object) {
    collection = object instanceof StateTimeCollector //
        ? new HashSet<>(((StateTimeCollector) object).getMembers()) //
        : Collections.emptySet();
  }
}
