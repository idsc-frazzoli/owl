// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
import ch.ethz.idsc.sophus.clt.LagrangeQuadraticD;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.win.AbstractDemo;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.sca.Exp;

/** The demo shows that when using LaneRiesenfeldCurveSubdivision(Clothoid.INSTANCE, degree)
 * in order to connect two points p and q, then the (odd) degree has little influence on the
 * resulting curve. The difference is only noticeable for S shaped curves.
 * 
 * Therefore, for simplicity in algorithms we use degree == 1. */
/* package */ class ClothoidStrokeDemo extends AbstractDemo {
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final Tensor DOMAIN = Subdivide.of(0.0, 1.0, 100);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = //
      ColorDataLists._097.cyclic().deriveWithAlpha(192);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    {
      graphics.setColor(new Color(255, 0, 0, 128));
      geometricLayer.pushMatrix(Se2Matrix.of(mouse));
      graphics.fill(geometricLayer.toPath2D(Arrowhead.of(0.3)));
      geometricLayer.popMatrix();
    }
    ClothoidBuilder clothoidBuilder = ClothoidBuilders.SE2_COVERING.clothoidBuilder();
    {
      Clothoid clothoid = clothoidBuilder.curve(START, mouse);
      Tensor points = DOMAIN.map(clothoid);
      Color color = COLOR_DATA_INDEXED.getColor(0);
      new PathRender(color, 1.5f) //
          .setCurve(points, false).render(geometricLayer, graphics);
      LagrangeQuadraticD lagrangeQuadraticD = clothoid.curvature();
      Tensor above = Tensors.empty();
      Tensor below = Tensors.empty();
      for (Tensor _t : DOMAIN) {
        Scalar t = _t.Get();
        Se2GroupElement se2GroupElement = new Se2GroupElement(clothoid.apply(t));
        Scalar curvature = lagrangeQuadraticD.apply(t);
        Scalar radius = Exp.FUNCTION.apply(curvature.multiply(curvature).negate());
        above.append(se2GroupElement.combine(Tensors.of(radius.zero(), radius, RealScalar.ZERO)));
        below.append(se2GroupElement.combine(Tensors.of(radius.zero(), radius.negate(), RealScalar.ZERO)));
      }
      new PathRender(color, 1.5f) //
          .setCurve(above, false).render(geometricLayer, graphics);
      new PathRender(color, 1.5f) //
          .setCurve(below, false).render(geometricLayer, graphics);
      Tensor tensor = Join.of(above, Reverse.of(below));
      graphics.fill(geometricLayer.toPath2D(tensor));
    }
  }

  public static void main(String[] args) {
    new ClothoidStrokeDemo().setVisible(1000, 600);
  }
}
