// code by jph, gjoel
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.tensor.Tensor;

public class LaneRender implements RenderInterface {
  private final PathRender pathRenderL = new PathRender(new Color(255, 128, 128, 192), 1);
  private final PathRender pathRenderR = new PathRender(new Color(128, 192, 128, 192), 1);
  // ---
  private final boolean cyclic;

  public LaneRender(boolean cyclic) {
    this.cyclic = cyclic;
  }

  public void setLane(LaneInterface lane) {
    if (Objects.nonNull(lane))
      setLanes(lane.leftBoundary(), lane.rightBoundary());
    else
      setLanes(null, null);
  }

  public void setLanes(Tensor lLane, Tensor rLane) {
    pathRenderL.setCurve(lLane, cyclic);
    pathRenderR.setCurve(rLane, cyclic);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    pathRenderL.render(geometricLayer, graphics);
    pathRenderR.render(geometricLayer, graphics);
  }
}
