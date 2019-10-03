// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.misc.CurveCurvatureRender;
import ch.ethz.idsc.sophus.crv.subdiv.HermiteSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LieMerrienHermiteSubdivision;
import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.AngleVector;

public class LieMerrienHermiteSubdivisionDemo extends ControlPointsDemo {
  public LieMerrienHermiteSubdivisionDemo() {
    super(true, GeodesicDisplays.SE2_ONLY);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor tensor = getControlPointsSe2();
    if (1 < tensor.length()) {
      Tensor control = Tensor.of(tensor.stream().map(xya -> Tensors.of(xya.extract(0, 2), AngleVector.of(xya.Get(2)))));
      // System.out.println(Dimensions.of(control));
      HermiteSubdivision hermiteSubdivision = //
          LieMerrienHermiteSubdivision.string(RnGroup.INSTANCE, RnExponential.INSTANCE, control);
      for (int count = 0; count < 5; ++count)
        hermiteSubdivision.iterate();
      Tensor iterate = hermiteSubdivision.iterate();
      Tensor curve = iterate.get(Tensor.ALL, 0);
      CurveCurvatureRender.of(curve, false, geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new LieMerrienHermiteSubdivisionDemo().setVisible(1200, 600);
  }
}
