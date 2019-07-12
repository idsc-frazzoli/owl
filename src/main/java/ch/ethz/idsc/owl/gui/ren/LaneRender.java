// code by jph, gjoel
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.Lane;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.tensor.Tensor;

public class LaneRender implements RenderInterface {
  private static final PathRender PATH_SIDE_L = new PathRender(new Color(255, 128, 128, 192), 1);
  private static final PathRender PATH_SIDE_R = new PathRender(new Color(128, 192, 128, 192), 1);
  // ---
  private final boolean cyclic;

  public LaneRender(boolean cyclic) {
    this.cyclic = cyclic;
  }

  public void setLane(Lane lane) {
    if (Objects.nonNull(lane))
      setLanes(lane.leftBoundary(), lane.rightBoundary());
    else
      setLanes(null, null);
  }

  public void setLanes(Tensor lLane, Tensor rLane) {
    PATH_SIDE_L.setCurve(lLane, cyclic);
    PATH_SIDE_R.setCurve(rLane, cyclic);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    PATH_SIDE_L.render(geometricLayer, graphics);
    PATH_SIDE_R.render(geometricLayer, graphics);
  }

}
