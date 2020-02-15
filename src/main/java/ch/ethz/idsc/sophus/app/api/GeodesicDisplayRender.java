// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public abstract class GeodesicDisplayRender implements RenderInterface {
  private static final Tensor CIRCLE = CirclePoints.of(61);
  private static final Color CENTER = new Color(255, 255, 255, 128);
  private static final Color BORDER = new Color(192, 192, 192, 128);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = getGeodesicDisplay();
    if (geodesicDisplay instanceof S1GeodesicDisplay) {
      S1GeodesicDisplay s1GeodesicDisplay = (S1GeodesicDisplay) geodesicDisplay;
      Scalar radius = s1GeodesicDisplay.getRadius();
      graphics.setColor(BORDER);
      graphics.draw(geometricLayer.toPath2D(CIRCLE.multiply(radius)));
    } else //
    if (geodesicDisplay instanceof S2GeodesicDisplay) {
      S2GeodesicDisplay s2GeodesicDisplay = (S2GeodesicDisplay) geodesicDisplay;
      Point2D center = geometricLayer.toPoint2D(0, 0);
      Scalar radius = s2GeodesicDisplay.getRadius();
      float fradius = geometricLayer.model2pixelWidth(radius.number().doubleValue());
      float[] dist = { 0.0f, 0.70f, 1.0f };
      Color[] colors = { CENTER, new Color(224, 224, 224, 128), BORDER };
      Paint paint = new RadialGradientPaint(center, fradius, dist, colors);
      graphics.setPaint(paint);
      graphics.fill(geometricLayer.toPath2D(CIRCLE.multiply(radius)));
    } else //
    if (geodesicDisplay instanceof H2GeodesicDisplay) {
      Paint paint = new GradientPaint( //
          geometricLayer.toPoint2D(0, 0), BORDER, //
          geometricLayer.toPoint2D(0, 5), CENTER);
      graphics.setPaint(paint);
      Tensor box = Tensors.fromString("{{-20, 0}, {+20, 0}, {+20, 5}, {-20, 5}}");
      graphics.fill(geometricLayer.toPath2D(box));
    }
  }

  public abstract GeodesicDisplay getGeodesicDisplay();
}
