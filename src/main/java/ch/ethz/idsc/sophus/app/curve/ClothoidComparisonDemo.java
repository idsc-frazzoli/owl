// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransition;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.api.PolarClothoidDisplay;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.crv.clothoid.LagrangeQuadraticD;
import ch.ethz.idsc.sophus.crv.clothoid.Se2CoveringClothoids;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/** demo compares conventional clothoid approximation with extended winding
 * number clothoid approximation to generate figures in report:
 * 
 * https://github.com/idsc-frazzoli/retina/files/3568308/20190903_appox_clothoids_with_ext_windings.pdf */
/* package */ class ClothoidComparisonDemo extends AbstractDemo implements DemoInterface {
  private static final int WIDTH = 480;
  private static final int HEIGHT = 360;
  private static final Tensor ARROWHEAD = Arrowhead.of(0.3);
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    GraphicsUtil.setQualityHigh(graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    {
      graphics.setColor(new Color(255, 0, 0, 255));
      geometricLayer.pushMatrix(Se2Matrix.of(Array.zeros(3)));
      graphics.draw(geometricLayer.toPath2D(ARROWHEAD, true));
      geometricLayer.popMatrix();
      geometricLayer.pushMatrix(Se2Matrix.of(mouse));
      graphics.draw(geometricLayer.toPath2D(ARROWHEAD, true));
      geometricLayer.popMatrix();
    }
    {
      graphics.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
      graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
      graphics.drawString("original", 0, 20);
      graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
      graphics.drawString("extended", 0, 34);
    }
    GeodesicDisplay geodesicDisplay = PolarClothoidDisplay.INSTANCE;
    // {
    // CurveSubdivision curveSubdivision = LaneRiesenfeldCurveSubdivision.of(PolarClothoids.INSTANCE, 3);
    // Tensor points = Nest.of(curveSubdivision::string, Tensors.of(START, mouse), 7);
    // new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f) //
    // .setCurve(points, false).render(geometricLayer, graphics);
    //
    // Tensor tensor = Tensor.of(points.stream().map(geodesicDisplay::toPoint));
    // CurveVisualSet curveVisualSet = new CurveVisualSet(tensor);
    // curveVisualSet.addCurvature();
    // VisualSet visualSet = curveVisualSet.visualSet();
    // JFreeChart jFreeChart = ListPlot.of(visualSet);
    // Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
    // jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
    // }
    {
      Clothoid clothoid = Se2CoveringClothoids.INSTANCE.curve(START, mouse);
      // LagrangeQuadraticD curvature = clothoid.curvature();
      Tensor points = Subdivide.of(0.0, 1.0, 20).map(clothoid);
      new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f) //
          .setCurve(points, false) //
          .render(geometricLayer, graphics);
    }
    {
      ClothoidTransition clothoidTransition = ClothoidTransition.of(START, mouse);
      Clothoid clothoid = clothoidTransition.clothoid();
      LagrangeQuadraticD curvature = clothoid.curvature();
      Tensor points = clothoidTransition.linearized(RealScalar.of(geometricLayer.pixel2modelWidth(5)));
      new PathRender(COLOR_DATA_INDEXED.getColor(1), 1.5f) //
          .setCurve(points, false) //
          .render(geometricLayer, graphics);
      // ---
      Tensor tensor = Tensor.of(points.stream().map(geodesicDisplay::toPoint));
      CurveVisualSet curveVisualSet = new CurveVisualSet(tensor);
      curveVisualSet.addCurvature();
      VisualSet visualSet = curveVisualSet.visualSet();
      {
        Tensor domain = curveVisualSet.getArcLength1();
        visualSet.add(domain, ConstantArray.of(curvature.head(), domain.length()));
        visualSet.add(domain, ConstantArray.of(curvature.tail(), domain.length()));
        visualSet.add(domain, Subdivide.of(0.0, 1.0, domain.length() - 1).map(curvature));
      }
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
      jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
    }
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new ClothoidComparisonDemo().setVisible(1000, 390);
  }
}
