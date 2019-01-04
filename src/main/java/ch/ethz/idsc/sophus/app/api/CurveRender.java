// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public class CurveRender implements RenderInterface {
  private final Tensor points;
  private final boolean isCyclic;
  private final Color color;
  private final Stroke stroke;

  /** @param points
   * @param isCyclic */
  public CurveRender(Tensor points, boolean isCyclic, Color color, float width) {
    this.points = points;
    this.isCyclic = isCyclic;
    this.color = color;
    stroke = new BasicStroke(width);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Path2D path2d = geometricLayer.toPath2D(points);
    if (isCyclic)
      path2d.closePath();
    graphics.setStroke(stroke);
    graphics.setColor(color);
    graphics.draw(path2d);
    graphics.setStroke(new BasicStroke(1f));
  }
}
