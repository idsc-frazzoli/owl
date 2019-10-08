// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.misc.CurveCurvatureRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.crv.subdiv.HermiteSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.HermiteSubdivisions;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.AngleVector;

public class HermiteSubdivisionDemo extends ControlPointsDemo {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  // ---
  private final SpinnerLabel<HermiteSubdivisions> spinnerLabelScheme = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("derivatives");

  public HermiteSubdivisionDemo() {
    super(true, GeodesicDisplays.SE2_R2);
    // ---
    {
      spinnerLabelScheme.setArray(HermiteSubdivisions.values());
      spinnerLabelScheme.setValue(HermiteSubdivisions.HERMITE1);
      spinnerLabelScheme.addToComponentReduced(timerFrame.jToolBar, new Dimension(140, 28), "scheme");
    }
    {
      spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerRefine.setValue(6);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
    timerFrame.jToolBar.addSeparator();
    {
      jToggleButton.setSelected(true);
      jToggleButton.setToolTipText("show derivatives");
      timerFrame.jToolBar.add(jToggleButton);
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor tensor = getControlPointsSe2();
    if (1 < tensor.length()) {
      GeodesicDisplay geodesicDisplay = geodesicDisplay();
      Tensor control;
      switch (geodesicDisplay.toString()) {
      case "SE2":
        control = Tensor.of(tensor.stream().map(xya -> Tensors.of(xya, UnitVector.of(3, 0))));
        break;
      case "R2":
        control = Tensor.of(tensor.stream().map(xya -> Tensors.of(xya.extract(0, 2), AngleVector.of(xya.Get(2)))));
        break;
      default:
        return;
      }
      HermiteSubdivision hermiteSubdivision = spinnerLabelScheme.getValue().supply(geodesicDisplay.lieGroup(), geodesicDisplay.lieExponential(),
          geodesicDisplay.biinvariantMean());
      TensorIteration tensorIteration = hermiteSubdivision.string(RealScalar.ONE, control);
      for (int count = 1; count < spinnerRefine.getValue(); ++count)
        tensorIteration.iterate();
      Tensor iterate = tensorIteration.iterate();
      Tensor curve = Tensor.of(iterate.get(Tensor.ALL, 0).stream().map(Extract2D.FUNCTION));
      CurveCurvatureRender.of(curve, false, geometricLayer, graphics);
      // ---
      if (jToggleButton.isSelected()) {
        Tensor deltas = iterate.get(Tensor.ALL, 1);
        if (0 < deltas.length()) {
          JFreeChart jFreeChart = StaticHelper.listPlot(deltas);
          Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
          jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
        }
      }
    }
  }

  public static void main(String[] args) {
    new HermiteSubdivisionDemo().setVisible(1200, 600);
  }
}
