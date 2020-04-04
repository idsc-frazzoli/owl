// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.Se2ClothoidDisplay;
import ch.ethz.idsc.sophus.crv.clothoid.Se2Clothoids;
import ch.ethz.idsc.sophus.crv.clothoid.Se2CoveringClothoids;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** The demo shows that when using LaneRiesenfeldCurveSubdivision(Clothoid.INSTANCE, degree)
 * in order to connect two points p and q, then the (odd) degree has little influence on the
 * resulting curve. The difference is only noticeable for S shaped curves.
 * 
 * Therefore, for simplicity in algorithms we use degree == 1. */
/* package */ class ClothoidDemo extends AbstractDemo implements DemoInterface {
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final Tensor DOMAIN = Subdivide.of(0.0, 1.0, 100);
  private static final Tensor ARROWS = Subdivide.of(0.0, 1.0, 10);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final PointsRender POINTS_RENDER_P = new PointsRender(new Color(0, 0, 0, 0), new Color(128, 128, 128, 64));
  private static final PointsRender POINTS_RENDER_C = new PointsRender(new Color(0, 0, 0, 0), new Color(128, 255, 128, 64));
  // ---
  private final JToggleButton jToggleButton = new JToggleButton("all");

  public ClothoidDemo() {
    jToggleButton.setSelected(true);
    timerFrame.jToolBar.add(jToggleButton);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    {
      graphics.setColor(new Color(255, 0, 0, 128));
      geometricLayer.pushMatrix(Se2Matrix.of(mouse));
      graphics.fill(geometricLayer.toPath2D(Arrowhead.of(.3)));
      geometricLayer.popMatrix();
    }
    {
      ScalarTensorFunction curve = Se2Clothoids.INSTANCE.curve(START, mouse);
      Tensor points = DOMAIN.map(curve);
      new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f) //
          .setCurve(points, false).render(geometricLayer, graphics);
      POINTS_RENDER_C.show(Se2ClothoidDisplay.INSTANCE::matrixLift, Arrowhead.of(0.3), ARROWS.map(curve)) //
          .render(geometricLayer, graphics);
    }
    if (jToggleButton.isSelected()) {
      ScalarTensorFunction curve = Se2CoveringClothoids.INSTANCE.curve(START, mouse);
      Tensor points = DOMAIN.map(curve);
      new PathRender(COLOR_DATA_INDEXED.getColor(2), 1.5f) //
          .setCurve(points, false).render(geometricLayer, graphics);
      POINTS_RENDER_P.show(Se2ClothoidDisplay.INSTANCE::matrixLift, Arrowhead.of(0.3), ARROWS.map(curve)) //
          .render(geometricLayer, graphics);
    }
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new ClothoidDemo().setVisible(1000, 600);
  }
}
