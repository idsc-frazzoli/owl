// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ani.TrajectoryListener;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class TrajectoryRender implements RenderInterface, TrajectoryListener {
  private static final Color COLOR_FLOW = new Color(64, 64, 64, 128);
  private static final Scalar U_SCALE = RealScalar.of(0.33);
  private static final Color COLOR_GROUND = new Color(255, 255, 255, 128);
  private static final Color COLOR_NODES = new Color(255, 0, 0, 96);
  private static final Color COLOR_TRAJECTORY = new Color(0, 192, 0, 192);
  // ---
  private List<TrajectorySample> trajectory;
  private Color color = COLOR_TRAJECTORY;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    List<TrajectorySample> list = trajectory;
    if (Objects.isNull(list))
      return;
    { // draw detailed trajectory from root to goal/furthestgo
      { // draw control vectors u along trajectory
        graphics.setColor(COLOR_FLOW);
        for (TrajectorySample trajectorySample : list) {
          Optional<Flow> flow = trajectorySample.getFlow();
          if (flow.isPresent()) {
            Tensor uscaled = flow.get().getU().multiply(U_SCALE);
            while (uscaled.length() < 2)
              uscaled.append(RealScalar.ZERO);
            graphics.draw(geometricLayer.toVector(trajectorySample.stateTime().state(), uscaled));
          }
        }
      }
      { // draw trajectory as thick green line with white background
        Path2D path2d = geometricLayer.toPath2D( //
            Tensor.of(list.stream() //
                .map(TrajectorySample::stateTime) //
                .map(StateTime::state)));
        graphics.setStroke(new BasicStroke(5.0f));
        graphics.setColor(COLOR_GROUND);
        graphics.draw(path2d);
        graphics.setStroke(new BasicStroke(2.0f));
        graphics.setColor(color);
        graphics.draw(path2d);
        graphics.setStroke(new BasicStroke());
      }
    }
    { // draw boxes at nodes in path from root to goal
      graphics.setColor(COLOR_NODES);
      list.stream().map(TrajectorySample::stateTime).map(StateTime::state).forEach(state -> {
        Point2D point2d = geometricLayer.toPoint2D(state);
        graphics.draw(new Rectangle2D.Double(point2d.getX() - 1, point2d.getY() - 1, 2, 2));
      });
    }
  }

  @Override
  public void trajectory(List<TrajectorySample> trajectory) {
    this.trajectory = trajectory;
  }

  public void setColor(Color color) {
    this.color = color;
  }
}
