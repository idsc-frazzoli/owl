// code by jph
package ch.ethz.idsc.sophus.gds;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.sca.Power;

public abstract class GeodesicDisplayRender implements RenderInterface {
  private static final Tensor CIRCLE = CirclePoints.of(61);
  private static final Color CENTER = new Color(255, 255, 255, 128);
  private static final Color BORDER = new Color(192, 192, 192, 128);
  private static final Tensor H1_DOMAIN = Subdivide.of(-2.0, 2.0, 20).map(Power.function(3));

  public static void render_s2(GeometricLayer geometricLayer, Graphics2D graphics) {
    Point2D center = geometricLayer.toPoint2D(0, 0);
    float fradius = geometricLayer.model2pixelWidth(1);
    float[] dist = { 0.0f, 0.70f, 1.0f };
    Color[] colors = { CENTER, new Color(224, 224, 224, 128), BORDER };
    Paint paint = new RadialGradientPaint(center, fradius, dist, colors);
    graphics.setPaint(paint);
    graphics.fill(geometricLayer.toPath2D(CIRCLE));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay geodesicDisplay = getGeodesicDisplay();
    if (geodesicDisplay instanceof S1Display) {
      graphics.setColor(BORDER);
      graphics.draw(geometricLayer.toPath2D(CIRCLE, true));
    } else //
    if (geodesicDisplay instanceof S2Display || //
        geodesicDisplay instanceof Rp2Display) {
      render_s2(geometricLayer, graphics);
    } else //
    // if (geodesicDisplay instanceof HP2GeodesicDisplay) {
    // Paint paint = new GradientPaint( //
    // geometricLayer.toPoint2D(0, 0), BORDER, //
    // geometricLayer.toPoint2D(0, 5), CENTER);
    // graphics.setPaint(paint);
    // Tensor box = Tensors.fromString("{{-20, 0}, {+20, 0}, {+20, 5}, {-20, 5}}");
    // graphics.fill(geometricLayer.toPath2D(box));
    // } else //
    if (geodesicDisplay instanceof H1Display) {
      Tensor points = //
          Tensor.of(H1_DOMAIN.map(Tensors::of).stream().map(HnWeierstrassCoordinate::toPoint));
      // ---
      graphics.setColor(BORDER);
      graphics.draw(geometricLayer.toPath2D(points));
    } else //
    if (geodesicDisplay instanceof H2Display) {
      double modelWidth = 5;
      Point2D center = geometricLayer.toPoint2D(0, 0);
      float fradius = geometricLayer.model2pixelWidth(modelWidth);
      float[] dist = { 0.0f, 0.30f, 1.0f };
      Color[] colors = { BORDER, new Color(224, 224, 224, 128), CENTER };
      Paint paint = new RadialGradientPaint(center, fradius, dist, colors);
      graphics.setPaint(paint);
      graphics.fill(geometricLayer.toPath2D(CIRCLE.multiply(RealScalar.of(modelWidth))));
      // ---
      // graphics.setColor(BORDER);
      // graphics.draw(geometricLayer.toPath2D(CIRCLE, true));
    } else //
    if (geodesicDisplay instanceof Spd2Display) {
      Point2D point2d = geometricLayer.toPoint2D(0, 0);
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(new Rectangle( //
          (int) point2d.getX(), //
          (int) point2d.getY(), 1, 1));
    }
  }

  public abstract ManifoldDisplay getGeodesicDisplay();
}
