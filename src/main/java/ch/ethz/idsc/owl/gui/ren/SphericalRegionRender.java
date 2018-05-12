// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.CirclePoints;

public enum SphericalRegionRender {
  ;
  private static final Tensor CIRCLE_POINTS = CirclePoints.of(16).unmodifiable();

  public static void draw(GeometricLayer geometricLayer, Graphics2D graphics, SphericalRegion sphericalRegion) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(sphericalRegion.center().extract(0, 2).append(RealScalar.ZERO)));
    graphics.setColor(Color.BLUE);
    // graphics.setPaint(new GradientPaint( //
    // geometricLayer.toPoint2D(Array.zeros(2)), NEAR, //
    // geometricLayer.toPoint2D(UnitVector.of(2, 0)), FAR));
    Path2D path2d = geometricLayer.toPath2D(CIRCLE_POINTS.multiply(sphericalRegion.radius()));
    // AngleVector.of(coneRegion.semi()), //
    // Array.zeros(2), //
    // AngleVector.of(coneRegion.semi().negate()) //
    // ));
    graphics.fill(path2d);
    geometricLayer.popMatrix();
  }
}
