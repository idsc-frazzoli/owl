// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ClothoidDisplay;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.api.PointsRender;
import ch.ethz.idsc.sophus.crv.clothoid.Se2Clothoids;
import ch.ethz.idsc.sophus.crv.clothoid.Se2CoveringClothoids;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** The demo shows that when using LaneRiesenfeldCurveSubdivision(Clothoid3.INSTANCE, degree)
 * in order to connect two points p and q, then the (odd) degree has little influence on the
 * resulting curve. The difference is only noticeable for S shaped curves.
 * 
 * Therefore, for simplicity in algorithms we use degree == 1. */
/* package */ class ClothoidSubdivisionDemo extends AbstractDemo implements DemoInterface {
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final Tensor DOMAIN = Subdivide.of(0.0, 1.0, 100);
  private static final Tensor ARROWS = Subdivide.of(0.0, 1.0, 10);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final PointsRender POINTS_RENDER_P = new PointsRender(new Color(0, 0, 0, 0), new Color(128, 128, 128, 64));
  private static final PointsRender POINTS_RENDER_C = new PointsRender(new Color(0, 0, 0, 0), new Color(128, 255, 128, 64));

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    {
      graphics.setColor(new Color(255, 0, 0, 128));
      geometricLayer.pushMatrix(Se2Matrix.of(mouse));
      graphics.fill(geometricLayer.toPath2D(Arrowhead.of(.3)));
      geometricLayer.popMatrix();
    }
    { // polar clothoid
      ScalarTensorFunction curve = Se2CoveringClothoids.INSTANCE.curve(START, mouse);
      new PathRender(COLOR_DATA_INDEXED.getColor(2), 1.5f) //
          .setCurve(DOMAIN.map(curve), false) //
          .render(geometricLayer, graphics);
      POINTS_RENDER_P.show(ClothoidDisplay.INSTANCE::matrixLift, Arrowhead.of(0.3), ARROWS.map(curve)) //
          .render(geometricLayer, graphics);
    }
    { // common clothoid
      ScalarTensorFunction curve = //
          Se2Clothoids.INSTANCE.curve(mouse.map(Scalar::zero), mouse);
      new PathRender(COLOR_DATA_INDEXED.getColor(3), 1.5f) //
          .setCurve(DOMAIN.map(curve), false) //
          .render(geometricLayer, graphics);
      POINTS_RENDER_C.show(ClothoidDisplay.INSTANCE::matrixLift, Arrowhead.of(0.3), ARROWS.map(curve)) //
          .render(geometricLayer, graphics);
    }
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new ClothoidSubdivisionDemo().setVisible(1000, 600);
  }
}
