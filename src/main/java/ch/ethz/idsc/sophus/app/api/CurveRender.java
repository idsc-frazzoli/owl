// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.CurvatureComb;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class CurveRender implements RenderInterface {
  private static final Color COLOR_CURVATURE_COMB = new Color(0, 0, 0, 128);
  private static final Scalar COMB_SCALE = DoubleScalar.of(1); // .5 (1 for presentation)
  // ---
  private final Tensor points;
  private final boolean isCyclic;
  private final boolean drawCurvature;

  /** @param points
   * @param isCyclic
   * @param curvature */
  public CurveRender(Tensor points, boolean isCyclic, boolean curvature) {
    this.points = points;
    this.isCyclic = isCyclic;
    this.drawCurvature = curvature;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    {
      Path2D path2d = geometricLayer.toPath2D(points);
      if (isCyclic)
        path2d.closePath();
      graphics.setStroke(new BasicStroke(1.25f));
      graphics.setColor(Color.BLUE);
      graphics.draw(path2d);
      graphics.setStroke(new BasicStroke(1f));
    }
    if (drawCurvature) {
      Path2D path2d = geometricLayer.toPath2D(CurvatureComb.of(points, COMB_SCALE, isCyclic));
      if (isCyclic)
        path2d.closePath();
      graphics.setColor(COLOR_CURVATURE_COMB);
      graphics.draw(path2d);
    }
  }
}
