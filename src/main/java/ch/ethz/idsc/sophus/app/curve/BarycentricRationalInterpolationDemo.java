// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.itp.BarycentricMetricInterpolation;
import ch.ethz.idsc.sophus.itp.BarycentricRationalInterpolation;
import ch.ethz.idsc.sophus.math.var.InversePowerVariogram;
import ch.ethz.idsc.sophus.math.win.KnotSpacing;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;

/* package */ class BarycentricRationalInterpolationDemo extends ControlPointsDemo {
  private static final int WIDTH = 400;
  private static final int HEIGHT = 300;
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final JToggleButton jToggleLagra = new JToggleButton("Lagr.");
  private final JToggleButton jToggleBasis = new JToggleButton("basis");

  public BarycentricRationalInterpolationDemo() {
    super(true, GeodesicDisplays.METRIC);
    {
      spinnerBeta.setList(Tensors.fromString("{0, 1/4, 1/2, 3/4, 1}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setIndex(0);
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
    }
    {
      spinnerDegree.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
      spinnerDegree.setValue(1);
      spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "degree");
    }
    timerFrame.jToolBar.add(jToggleLagra);
    {
      jToggleBasis.setSelected(true);
      timerFrame.jToolBar.add(jToggleBasis);
    }
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {2, 0, 0}, {4, 3, 1}, {5, -1, -2}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    TensorUnaryOperator tensorUnaryOperator = //
        KnotSpacing.centripetal(geodesicDisplay.parametricDistance(), spinnerBeta.getValue());
    Tensor knots = tensorUnaryOperator.apply(control);
    if (1 < control.length()) {
      Tensor domain = Subdivide.of(knots.get(0), Last.of(knots), 25 * control.length());
      BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
      Tensor basis2 = domain.map(jToggleLagra.isSelected() //
          ? BarycentricMetricInterpolation.la(knots, InversePowerVariogram.of(2))
          : BarycentricMetricInterpolation.of(knots, InversePowerVariogram.of(2)));
      try {
        Tensor curve = Tensor.of(basis2.stream().map(weights -> biinvariantMean.mean(control, weights)));
        new PathRender(Color.RED) //
            .setCurve(curve, false) //
            .render(geometricLayer, graphics);
      } catch (Exception exception) {
        System.err.println("no can do");
      }
      Tensor basis1 = domain.map(BarycentricRationalInterpolation.of(knots, spinnerDegree.getValue()));
      try {
        Tensor curve = Tensor.of(basis1.stream().map(weights -> biinvariantMean.mean(control, weights)));
        new PathRender(Color.BLUE) //
            .setCurve(curve, false) //
            .render(geometricLayer, graphics);
      } catch (Exception exception) {
        System.err.println("no can do");
      }
      if (jToggleBasis.isSelected()) {
        {
          VisualSet visualSet = new VisualSet();
          for (Tensor values : Transpose.of(basis2))
            visualSet.add(domain, values);
          JFreeChart jFreeChart = ListPlot.of(visualSet);
          Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
          jFreeChart.draw(graphics, new Rectangle(dimension.width - WIDTH, dimension.height - HEIGHT, WIDTH, HEIGHT));
        }
        {
          VisualSet visualSet = new VisualSet();
          for (Tensor values : Transpose.of(basis1))
            visualSet.add(domain, values);
          JFreeChart jFreeChart = ListPlot.of(visualSet);
          Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
          jFreeChart.draw(graphics, new Rectangle(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
        }
      }
    }
    // ---
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new BarycentricRationalInterpolationDemo().setVisible(1200, 600);
  }
}
