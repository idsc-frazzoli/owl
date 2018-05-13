// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.CirclePoints;

public enum SphericalRegionRender {
  ;
  private static final Tensor CIRCLE_POINTS = CirclePoints.of(16).unmodifiable();
  private static final float[] RATIOS = { 0.0f, 1.0f };
  private static final Color[] COLORS = { new Color(255, 0, 0, 64), new Color(255, 255, 0, 64) };

  public static void draw(GeometricLayer geometricLayer, Graphics2D graphics, SphericalRegion sphericalRegion) {
    Tensor polygon = CIRCLE_POINTS.multiply(sphericalRegion.radius());
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix( //
        sphericalRegion.center().extract(0, 2).append(RealScalar.ZERO)));
    Point2D center = geometricLayer.toPoint2D(Array.zeros(2));
    Point2D extent = geometricLayer.toPoint2D(polygon.get(0));
    graphics.setPaint(new RadialGradientPaint( //
        center, (float) extent.distance(center), //
        RATIOS, COLORS));
    graphics.fill(geometricLayer.toPath2D(polygon));
    geometricLayer.popMatrix();
  }
}
