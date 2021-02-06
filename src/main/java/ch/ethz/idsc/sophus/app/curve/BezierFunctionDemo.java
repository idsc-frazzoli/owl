// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.crv.bezier.BezierFunction;
import ch.ethz.idsc.sophus.gds.GeodesicDisplay;
import ch.ethz.idsc.sophus.gui.ren.Curvature2DRender;
import ch.ethz.idsc.sophus.gui.win.DubinsGenerator;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;

/** Bezier function with extrapolation */
/* package */ class BezierFunctionDemo extends CurvatureDemo {
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();

  public BezierFunctionDemo() {
    addButtonDubins();
    // ---
    spinnerRefine.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    spinnerRefine.setValue(8);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    {
      Tensor tensor = Tensors.fromString("{{1, 0, 0}, {2, 0, 2.5708}, {1, 0, 2.1}, {1.5, 0, 0}, {2.3, 0, -1.2}}");
      setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
          Tensor.of(tensor.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
    }
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    // ---
    Tensor control = getGeodesicControlPoints();
    int n = control.length();
    if (0 == n)
      return Tensors.empty();
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(geodesicDisplay.geodesicInterface(), control);
    int levels = spinnerRefine.getValue();
    Tensor domain = n <= 1 //
        ? Tensors.vector(0)
        : Subdivide.of(0, n / (double) (n - 1), 1 << levels);
    Tensor refined = domain.map(scalarTensorFunction);
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
