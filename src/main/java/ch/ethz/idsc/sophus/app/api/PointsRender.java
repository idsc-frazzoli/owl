// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class PointsRender {
  private final Color color_fill;
  private final Color color_draw;

  public PointsRender(Color color_fill, Color color_draw) {
    this.color_fill = color_fill;
    this.color_draw = color_draw;
  }

  // TODO design as WaypointRender
  public class Show implements RenderInterface {
    private final GeodesicDisplay geodesicDisplay;
    private final Tensor points;

    public Show(GeodesicDisplay geodesicDisplay, Tensor points) {
      this.geodesicDisplay = geodesicDisplay;
      this.points = points;
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      Tensor shape = geodesicDisplay.shape();
      for (Tensor point : points) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(color_fill);
        graphics.fill(path2d);
        graphics.setColor(color_draw);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
  }
}
