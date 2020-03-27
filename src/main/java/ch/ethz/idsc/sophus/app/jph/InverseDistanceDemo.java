// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.hs.HsBarycentricCoordinate;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.AffineQ;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class InverseDistanceDemo extends ControlPointsDemo {
  public InverseDistanceDemo() {
    super(true, GeodesicDisplays.SE2C_SPD2_S2_Rn);
    setControlPointsSe2(Tensors.fromString("{{-1, 0, 0}, {3, 0, 0}, {2, 3, 1}, {5, -1, 2}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor domain = getGeodesicControlPoints();
    renderControlPoints(geometricLayer, graphics);
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    BarycentricCoordinate barycentricCoordinate = HsBarycentricCoordinate.smooth(geodesicDisplay.flattenLogManifold());
    Tensor point = geodesicDisplay.project(geometricLayer.getMouseSe2State());
    if (geodesicDisplay.dimensions() < domain.length()) {
      Tensor weights = barycentricCoordinate.weights(domain, point);
      AffineQ.require(weights);
      Tensor mean = biinvariantMean.mean(getGeodesicControlPoints(), weights);
      Tensor matrix = geodesicDisplay.matrixLift(mean);
      geometricLayer.pushMatrix(matrix);
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape().multiply(RealScalar.of(0.7)));
      graphics.setColor(Color.GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    new InverseDistanceDemo().setVisible(1200, 600);
  }
}
