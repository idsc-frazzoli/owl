// code by jph, gjoel
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.JToggleButton;

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
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid1;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid2;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid3;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatio;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
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
  private final SpinnerLabel<Integer> spinnerBegin = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLevel = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("signed curv.");
  private final List<SplitInterface> splitInterfaces = //
      Arrays.asList(Clothoid1.INSTANCE, Clothoid2.INSTANCE, Clothoid3.INSTANCE);

  public ClothoidCurvatureDemo() {
    spinnerBegin.setArray(0, 1, 2);
    spinnerBegin.setIndex(2);
    spinnerBegin.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "begin");
    // ---
    spinnerLevel.setArray(1, 2, 3, 4, 5, 6, 7, 8);
    spinnerLevel.setIndex(4);
    spinnerLevel.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "levels");
    // ---
    jToggleButton.setToolTipText("display signed curvature result");
    timerFrame.jToolBar.add(jToggleButton);
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
    VisualSet visualSet = new VisualSet();
    for (int index = spinnerBegin.getValue(); index < splitInterfaces.size(); ++index)
      innerRender(splitInterfaces.get(index), geometricLayer, graphics, visualSet, index);
    int n = (int) Math.pow(2, spinnerLevel.getValue());
    {
      ClothoidTerminalRatio clothoidTerminalRatios = ClothoidTerminalRatios.of(START, mouse);
      Scalar head = clothoidTerminalRatios.head();
      Scalar tail = clothoidTerminalRatios.tail();
      {
        VisualRow visualRow = visualSet.add(Tensors.vector(0, n), Tensors.of(head, head));
        visualRow.setColor(Color.BLACK);
      }
      {
        VisualRow visualRow = visualSet.add(Tensors.vector(0, n), Tensors.of(tail, tail));
        visualRow.setColor(Color.BLACK);
      }
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet);
    Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
    jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
  }

  private void innerRender(SplitInterface splitInterface, GeometricLayer geometricLayer, Graphics2D graphics, VisualSet visualSet, int nr) {
    Tensor mouse = geometricLayer.getMouseSe2State();
    Color color = COLOR_DATA_INDEXED.getColor(nr);
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(splitInterface, 1);
    Tensor points = Nest.of(curveSubdivision::string, Tensors.of(START, mouse), spinnerLevel.getValue());
    graphics.setColor(color);
    graphics.drawString(splitInterface.getClass().getSimpleName(), 0, (nr + 2) * 10);
    new PathRender(color, 1.5f) //
        .setCurve(points, false).render(geometricLayer, graphics);
    if (jToggleButton.isSelected()) {
      Tensor curvature = CurveCurvature.string(Tensor.of(points.stream().map(Extract2D.FUNCTION)));
      VisualRow visualRow = visualSet.add(Range.of(0, curvature.length()), curvature);
      visualRow.setColor(color);
    }
    {
      Tensor curvature = ClothoidCurvatures.of(points);
      VisualRow visualRow = visualSet.add(Range.of(0, curvature.length()), curvature);
      visualRow.setColor(color);
    }
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
