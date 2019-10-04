// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.misc.CurveCurvatureRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.crv.subdiv.HermiteSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LieMerrienHermiteSubdivision;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.AngleVector;

public class LieMerrienHermiteSubdivisionDemo extends ControlPointsDemo {
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();

  public LieMerrienHermiteSubdivisionDemo() {
    super(true, GeodesicDisplays.SE2_R2);
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRefine.setValue(6);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
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
      default:
        return;
      }
      HermiteSubdivision hermiteSubdivision = //
          new LieMerrienHermiteSubdivision(geodesicDisplay.lieGroup(), geodesicDisplay.lieExponential()).string(control);
      for (int count = 1; count < spinnerRefine.getValue(); ++count)
        hermiteSubdivision.iterate();
      Tensor iterate = hermiteSubdivision.iterate();
      Tensor curve = Tensor.of(iterate.get(Tensor.ALL, 0).stream().map(Extract2D.FUNCTION));
      CurveCurvatureRender.of(curve, false, geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new LieMerrienHermiteSubdivisionDemo().setVisible(1200, 600);
  }
}
