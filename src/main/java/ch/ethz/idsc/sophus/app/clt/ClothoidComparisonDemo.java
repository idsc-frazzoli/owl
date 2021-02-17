// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransition;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.LagrangeQuadraticD;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.Se2CoveringClothoidDisplay;
import ch.ethz.idsc.sophus.gui.ren.CurveVisualSet;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.win.AbstractDemo;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
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
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    ManifoldDisplay geodesicDisplay = Se2CoveringClothoidDisplay.INSTANCE;
    {
      Tensor shape = geodesicDisplay.shape();
      geometricLayer.pushMatrix(Se2Matrix.of(Array.zeros(3)));
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      graphics.setColor(new Color(255, 0, 0, 64));
      graphics.fill(path2d);
      graphics.setColor(new Color(255, 0, 0, 255));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
      // ---
      geometricLayer.pushMatrix(Se2Matrix.of(mouse));
      graphics.draw(geometricLayer.toPath2D(shape, true));
      geometricLayer.popMatrix();
    }
    VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(192));
    for (ClothoidTransitionSpace clothoidTransitionSpace : ClothoidTransitionSpace.values()) {
      int ordinal = clothoidTransitionSpace.ordinal();
      Color color = COLOR_DATA_INDEXED.getColor(ordinal);
      {
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        graphics.setColor(color);
        graphics.drawString(clothoidTransitionSpace.name(), 0, 24 + ordinal * 14);
      }
      ClothoidTransition clothoidTransition = clothoidTransitionSpace.connect(START, mouse);
      Clothoid clothoid = clothoidTransition.clothoid();
      Tensor points = clothoidTransition.linearized(RealScalar.of(geometricLayer.pixel2modelWidth(5)));
      new PathRender(color, 1.5f).setCurve(points, false).render(geometricLayer, graphics);
      // ---
      Tensor tensor = Tensor.of(points.stream().map(geodesicDisplay::toPoint));
      CurveVisualSet curveVisualSet = new CurveVisualSet(tensor);
      curveVisualSet.addCurvature(visualSet);
      {
        LagrangeQuadraticD curvature = clothoid.curvature();
        Tensor domain = curveVisualSet.getArcLength1();
        visualSet.add(domain, ConstantArray.of(curvature.head(), domain.length()));
        visualSet.add(domain, ConstantArray.of(curvature.tail(), domain.length()));
        visualSet.add(domain, Subdivide.of(0.0, 1.0, domain.length() - 1).map(curvature));
        visualSet.add(domain, Subdivide.of(0.0, 1.0, domain.length() - 1).map(clothoid::addAngle));
      }
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
    jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new ClothoidComparisonDemo().setVisible(1000, 390);
  }
}
