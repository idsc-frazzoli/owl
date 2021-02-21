// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.flt.ga.GeodesicMeanFilter;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.Curvature2DRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.gui.win.DubinsGenerator;
import ch.ethz.idsc.sophus.ref.d1.BSpline4CurveSubdivision;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;

/* package */ class GeodesicMeanFilterDemo extends ControlPointsDemo {
  private final SpinnerLabel<Integer> spinnerRadius = new SpinnerLabel<>();

  GeodesicMeanFilterDemo() {
    super(true, GeodesicDisplays.ALL);
    {
      Tensor tensor = Tensors.fromString("{{1, 0, 0}, {2, 0, 2.5708}, {1, 0, 2.1}, {1.5, 0, 0}, {2.3, 0, -1.2}, {1.5, 0, 0}}");
      setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
          Tensor.of(tensor.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
    }
    // ---
    spinnerRadius.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRadius.setValue(9);
    spinnerRadius.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
  }

  @Override // from RenderInterface
  public synchronized void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    int radius = spinnerRadius.getValue();
    renderControlPoints(geometricLayer, graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    TensorUnaryOperator geodesicMeanFilter = GeodesicMeanFilter.of(geodesicDisplay.geodesicInterface(), radius);
    Tensor refined = geodesicMeanFilter.apply(control);
    Tensor curve = Nest.of(BSpline4CurveSubdivision.split2lo(geodesicDisplay.geodesicInterface())::string, refined, 7);
    Tensor render = Tensor.of(curve.stream().map(geodesicDisplay::toPoint));
    Curvature2DRender.of(render, false, geometricLayer, graphics);
    renderPoints(geodesicDisplay, refined, geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new GeodesicMeanFilterDemo().setVisible(1000, 600);
  }
}
