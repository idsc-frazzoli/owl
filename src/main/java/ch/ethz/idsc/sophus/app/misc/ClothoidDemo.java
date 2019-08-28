// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid3;
import ch.ethz.idsc.sophus.crv.clothoid.CommonClothoids;
import ch.ethz.idsc.sophus.crv.clothoid.PolarClothoids;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Nest;

/** The demo shows that when using LaneRiesenfeldCurveSubdivision(Clothoid3.INSTANCE, degree)
 * in order to connect two points p and q, then the (odd) degree has little influence on the
 * resulting curve. The difference is only noticeable for S shaped curves.
 * 
 * Therefore, for simplicity in algorithms we use degree == 1. */
public class ClothoidDemo extends AbstractDemo implements DemoInterface {
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final Tensor DOMAIN = Subdivide.of(0.0, 1.0, 100);
  private static final Tensor ARROWS = Subdivide.of(0.0, 1.0, 10);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);

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
    {
      Tensor points = DOMAIN.map(Clothoid3.INSTANCE.curve(START, mouse));
      new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f) //
          .setCurve(points, false).render(geometricLayer, graphics);
    }
    int count = 1;
    for (int degree = 1; degree <= 5; degree += 2) {
      CurveSubdivision curveSubdivision = LaneRiesenfeldCurveSubdivision.of(Clothoid3.INSTANCE, degree);
      Tensor points = Nest.of(curveSubdivision::string, Tensors.of(START, mouse), 6);
      new PathRender(COLOR_DATA_INDEXED.getColor(count), 1.5f) //
          .setCurve(points, false).render(geometricLayer, graphics);
      ++count;
    }
    {
      ScalarTensorFunction curve = //
          PolarClothoids.INSTANCE.curve(mouse.map(Scalar::zero), mouse);
      {
        Tensor points = DOMAIN.map(curve);
        new PathRender(COLOR_DATA_INDEXED.getColor(2), 1.5f) //
            .setCurve(points, false).render(geometricLayer, graphics);
      }
      {
        graphics.setColor(new Color(128, 128, 128, 64));
        Tensor points = ARROWS.map(curve);
        for (Tensor xya : points) {
          geometricLayer.pushMatrix(Se2Matrix.of(xya));
          Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(0.3), true);
          graphics.draw(path2d);
          // new PathRender(COLOR_DATA_INDEXED.getColor(2), 1.5f) //
          // .setCurve(points, false).render(geometricLayer, graphics);
          geometricLayer.popMatrix();
        }
      }
    }
    {
      ScalarTensorFunction curve = //
          CommonClothoids.INSTANCE.curve(mouse.map(Scalar::zero), mouse);
      {
        Tensor points = DOMAIN.map(curve);
        new PathRender(COLOR_DATA_INDEXED.getColor(3), 1.5f) //
            .setCurve(points, false).render(geometricLayer, graphics);
      }
      {
        graphics.setColor(new Color(128, 128, 128, 64));
        Tensor points = ARROWS.map(curve);
        for (Tensor xya : points) {
          geometricLayer.pushMatrix(Se2Matrix.of(xya));
          Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(0.3), true);
          graphics.draw(path2d);
          // new PathRender(COLOR_DATA_INDEXED.getColor(2), 1.5f) //
          // .setCurve(points, false).render(geometricLayer, graphics);
          geometricLayer.popMatrix();
        }
      }
    }
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new ClothoidDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
