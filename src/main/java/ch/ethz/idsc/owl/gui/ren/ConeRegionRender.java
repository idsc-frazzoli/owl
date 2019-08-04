// code by jph
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.AngleVector;

public enum ConeRegionRender {
  ;
  private static final Color NEAR = new Color(255, 0, 0, 64);
  private static final Color FAR = new Color(255, 255, 0, 64);

  public static void draw(GeometricLayer geometricLayer, Graphics2D graphics, ConeRegion coneRegion) {
    geometricLayer.pushMatrix(Se2Matrix.of(coneRegion.apex()));
    graphics.setPaint(new GradientPaint( //
        geometricLayer.toPoint2D(Array.zeros(2)), NEAR, //
        geometricLayer.toPoint2D(UnitVector.of(2, 0)), FAR));
    Path2D path2d = geometricLayer.toPath2D(Tensors.of( //
        AngleVector.of(coneRegion.semi()), //
        Array.zeros(2), //
        AngleVector.of(coneRegion.semi().negate()) //
    ));
    graphics.fill(path2d);
    geometricLayer.popMatrix();
  }
}
