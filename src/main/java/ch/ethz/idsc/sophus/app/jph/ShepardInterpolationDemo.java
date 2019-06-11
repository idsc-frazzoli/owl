// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.sophus.math.ShepardInterpolation;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm2Squared;

public class ShepardInterpolationDemo extends ControlPointsDemo {
  public ShepardInterpolationDemo() {
    super(true, GeodesicDisplays.SE2C_SE2_R2);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPointsSe2 = getControlPointsSe2();
    renderControlPoints(geometricLayer, graphics);
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    ShepardInterpolation shepardInterpolation = //
        new ShepardInterpolation(Norm2Squared::between, geodesicDisplay.biinvariantMean());
    Tensor domain = Tensor.of(controlPointsSe2.stream().map(Extract2D.FUNCTION));
    Tensor point = geometricLayer.getMouseSe2State().extract(0, 2);
    if (0 < controlPointsSe2.length()) {
      Tensor weights = shepardInterpolation.weights(domain, point);
      // geodesicDisplay.project(xya);
      Tensor mean = biinvariantMean.mean(getGeodesicControlPoints(), weights);
      // System.out.println(mean);
      Tensor matrix = geodesicDisplay.matrixLift(mean);
      geometricLayer.pushMatrix(matrix);
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape().multiply(RealScalar.of(0.7)));
      graphics.setColor(Color.GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new ShepardInterpolationDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
