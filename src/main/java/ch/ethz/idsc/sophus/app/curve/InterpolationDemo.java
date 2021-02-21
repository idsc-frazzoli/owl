// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.crv.spline.BSplineLimitMatrix;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.ref.d1.BSpline3CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.CurveSubdivision;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.red.Nest;

// TODO functionality/purpose is not clear
/* package */ class InterpolationDemo extends ControlPointsDemo {
  public InterpolationDemo() {
    super(true, GeodesicDisplays.SE2C_SE2_R2);
    // ---
    addButtonDubins();
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderControlPoints(geometricLayer, graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    Tensor sequence = getGeodesicControlPoints();
    if (0 < sequence.length()) {
      Tensor matrix = BSplineLimitMatrix.string(sequence.length(), 3);
      Tensor invers = Inverse.of(matrix);
      Tensor tensor = Tensor.of(invers.stream().map(weights -> biinvariantMean.mean(sequence, weights)));
      CurveSubdivision curveSubdivision = new BSpline3CurveSubdivision(geodesicDisplay.geodesicInterface());
      Tensor refine = Nest.of(curveSubdivision::string, tensor, 5);
      new PathRender(Color.BLUE).setCurve(refine, false).render(geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new InterpolationDemo().setVisible(1000, 800);
  }
}
