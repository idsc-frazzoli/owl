// code by jph
package ch.ethz.idsc.sophus.app.hermite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.clt.ClothoidDistance;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.Se2Display;
import ch.ethz.idsc.sophus.gui.ren.Curvature2DRender;
import ch.ethz.idsc.sophus.gui.ren.PointsRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.math.Distances;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.sophus.opt.HermiteSubdivisions;
import ch.ethz.idsc.sophus.ref.d1h.HermiteSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.r2.AngleVector;
import ch.ethz.idsc.tensor.red.Mean;

/* package */ class HermiteSubdivisionDemo extends ControlPointsDemo {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  // ---
  private final SpinnerLabel<HermiteSubdivisions> spinnerLabelScheme = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("derivatives");

  public HermiteSubdivisionDemo() {
    super(true, GeodesicDisplays.SE2C_SE2_R2);
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

  private static final PointsRender POINTS_RENDER_0 = //
      new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  private static final GridRender GRID_RENDER = new GridRender(Subdivide.of(0, 10, 10));

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GRID_RENDER.render(geometricLayer, graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    final Tensor tensor = getControlPointsSe2();
    POINTS_RENDER_0.show(Se2Display.INSTANCE::matrixLift, //
        Se2Display.INSTANCE.shape(), //
        tensor).render(geometricLayer, graphics);
    // renderControlPoints(geometricLayer, graphics);
    if (1 < tensor.length()) {
      ManifoldDisplay geodesicDisplay = manifoldDisplay();
      Tensor control;
      switch (geodesicDisplay.toString()) {
      case "SE2C":
      case "SE2":
        // TODO use various options: unit vector, scaled by parametric distance, ...
        control = Tensor.of(tensor.stream().map(xya -> Tensors.of(xya, UnitVector.of(3, 0))));
        break;
      case "R2":
        // TODO use various options: unit vector, scaled by parametric distance, ...
        control = Tensor.of(tensor.stream().map(xya -> Tensors.of(xya.extract(0, 2), AngleVector.of(xya.Get(2)))));
        break;
      default:
        return;
      }
      {
        Tensor distances = Distances.of(ClothoidDistance.SE2_ANALYTIC, tensor);
        // Distances.of(geodesicDisplay::parametricDistance, control.get(Tensor.ALL, 0));
        if (0 < distances.length()) {
          Tensor scaling = Array.zeros(control.length());
          scaling.set(distances.get(0), 0);
          for (int index = 1; index < distances.length(); ++index)
            scaling.set((Scalar) Mean.of(distances.extract(index - 1, index + 1)), index);
          scaling.set((Scalar) Last.of(distances), control.length() - 1);
          // ---
          for (int index = 0; index < control.length(); ++index) {
            int fi = index;
            control.set(t -> t.multiply(scaling.Get(fi)), index, 1);
          }
        }
      }
      HermiteSubdivision hermiteSubdivision = spinnerLabelScheme.getValue().supply( //
          geodesicDisplay.hsManifold(), //
          geodesicDisplay.hsTransport(), //
          geodesicDisplay.biinvariantMean());
      TensorIteration tensorIteration = hermiteSubdivision.string(RealScalar.ONE, control);
      int levels = spinnerRefine.getValue();
      Tensor iterate = Do.of(control, tensorIteration::iterate, levels);
      Tensor curve = Tensor.of(iterate.get(Tensor.ALL, 0).stream().map(Extract2D.FUNCTION));
      Curvature2DRender.of(curve, false, geometricLayer, graphics);
      {
        Scalar scale = RealScalar.of(0.3);
        switch (geodesicDisplay.toString()) {
        case "SE2C":
        case "SE2":
          new Se2HermitePlot(iterate, scale).render(geometricLayer, graphics);
          break;
        case "R2":
          new R2HermitePlot(iterate, scale).render(geometricLayer, graphics);
          break;
        }
      }
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
