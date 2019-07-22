// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.crv.CurveCurvature;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidCurvature;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.red.Nest;

public class ClothoidCurvatureDemo extends AbstractDemo implements DemoInterface {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private final SpinnerLabel<Integer> spinnerLevel = new SpinnerLabel<>();

  public ClothoidCurvatureDemo() {
    spinnerLevel.setArray(1, 2, 3, 4, 5, 6, 7, 8);
    spinnerLevel.setIndex(2);
    spinnerLevel.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "levels");
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    {
      graphics.setColor(new Color(255, 0, 0, 128));
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(mouse));
      graphics.fill(geometricLayer.toPath2D(Arrowhead.of(.3)));
      geometricLayer.popMatrix();
    }
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(Clothoid.INSTANCE, 1);
    Tensor points = Nest.of(curveSubdivision::string, Tensors.of(START, mouse), spinnerLevel.getValue());
    new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f) //
        .setCurve(points, false).render(geometricLayer, graphics);
    VisualSet visualSet = new VisualSet();
    {
      Tensor curvature = CurveCurvature.string(Tensor.of(points.stream().map(Extract2D.FUNCTION)));
      visualSet.add(Range.of(0, curvature.length()), curvature);
    }
    {
      Tensor p = points.get(0);
      Tensor curvature = Tensors.empty();
      ClothoidCurvature clothoidCurvature = null;
      for (int index = 1; index < points.length(); ++index) {
        Tensor q = points.get(index);
        clothoidCurvature = new ClothoidCurvature(p, q);
        curvature.append(clothoidCurvature.head());
        p = q;
      }
      curvature.append(clothoidCurvature.tail());
      visualSet.add(Range.of(0, curvature.length()), curvature);
    }
    {
      ClothoidTerminalRatios clothoidTerminalRatios = ClothoidTerminalRatios.of(START, mouse);
      Scalar head = clothoidTerminalRatios.head();
      visualSet.add(Tensors.vector(0, points.length() - 1), Tensors.of(head, head));
    }
    {
      ClothoidTerminalRatios clothoidTerminalRatios = ClothoidTerminalRatios.of(START, mouse);
      Scalar tail = clothoidTerminalRatios.tail();
      visualSet.add(Tensors.vector(0, points.length() - 1), Tensors.of(tail, tail));
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
    AbstractDemo abstractDemo = new ClothoidCurvatureDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
