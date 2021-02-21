// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.crv.bezier.BezierFunction;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.Se2Display;
import ch.ethz.idsc.sophus.gui.ren.Curvature2DRender;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.win.DubinsGenerator;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;

/** Bezier function with extrapolation */
/* package */ class BezierFunctionDemo extends AbstractCurvatureDemo {
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("extrap.");

  public BezierFunctionDemo() {
    addButtonDubins();
    // ---
    spinnerRefine.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    spinnerRefine.setValue(6);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    timerFrame.jToolBar.addSeparator();
    {
      timerFrame.jToolBar.add(jToggleButton);
    }
    {
      Tensor tensor = Tensors.fromString("{{1, 0, 0}, {2, 0, 2.5708}, {1, 0, 2.1}, {1.5, 0, 0}, {2.3, 0, -1.2}}");
      setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
          Tensor.of(tensor.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
    }
    setGeodesicDisplay(Se2Display.INSTANCE);
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    // ---
    Tensor sequence = getGeodesicControlPoints();
    int n = sequence.length();
    if (0 == n)
      return Tensors.empty();
    int levels = spinnerRefine.getValue();
    Tensor domain = n <= 1 //
        ? Tensors.vector(0)
        : Subdivide.of(0.0, jToggleButton.isSelected() //
            ? n / (double) (n - 1)
            : 1.0, 1 << levels);
    {
      BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
      if (Objects.nonNull(biinvariantMean)) {
        Tensor refined = domain.map(BezierFunction.of(biinvariantMean, sequence));
        Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
        new PathRender(Color.RED, 1.25f).setCurve(render, false).render(geometricLayer, graphics);
      }
    }
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    Tensor refined = domain.map(BezierFunction.of(geodesicInterface, sequence));
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    Curvature2DRender.of(render, false, geometricLayer, graphics);
    if (levels < 5)
      renderPoints(geodesicDisplay, refined, geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    new BezierFunctionDemo().setVisible(1000, 600);
  }
}
