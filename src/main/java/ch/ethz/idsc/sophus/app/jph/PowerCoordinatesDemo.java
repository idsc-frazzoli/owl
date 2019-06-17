// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.Polygons;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.crd.Barycentric;
import ch.ethz.idsc.sophus.math.crd.PowerCoordinates;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ConvexHull;
import ch.ethz.idsc.tensor.red.Entrywise;

public class PowerCoordinatesDemo extends ControlPointsDemo {
  private final SpinnerLabel<Barycentric> spinnerBarycentric = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();

  public PowerCoordinatesDemo() {
    super(true, GeodesicDisplays.SE2C_SE2_R2);
    {
      spinnerBarycentric.setArray(Barycentric.values());
      spinnerBarycentric.setIndex(0);
      spinnerBarycentric.addToComponentReduced(timerFrame.jToolBar, new Dimension(170, 28), "barycentric");
    }
    {
      spinnerRefine.setList(Arrays.asList(10, 20, 30, 40));
      spinnerRefine.setIndex(0);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPointsSe2 = getGeodesicControlPoints();
    renderControlPoints(geometricLayer, graphics);
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    Tensor domain = Tensor.of(controlPointsSe2.stream().map(Extract2D.FUNCTION));
    if (2 < domain.length()) {
      Tensor hull = ConvexHull.of(domain);
      graphics.setColor(Color.BLACK);
      Path2D path2d = geometricLayer.toPath2D(hull);
      path2d.closePath();
      graphics.draw(path2d);
      PowerCoordinates powerCoordinates = new PowerCoordinates(spinnerBarycentric.getValue());
      Tensor min = hull.stream().reduce(Entrywise.min()).get().map(RealScalar.of(0.1)::add);
      Tensor max = hull.stream().reduce(Entrywise.max()).get().map(RealScalar.of(0.1)::subtract).negate();
      Tensor sX = Subdivide.of(min.Get(0), max.Get(0), spinnerRefine.getValue());
      Tensor sY = Subdivide.of(min.Get(1), max.Get(1), spinnerRefine.getValue());
      for (Tensor _x : sX)
        for (Tensor _y : sY) {
          Scalar x = _x.Get();
          Scalar y = _y.Get();
          Tensor px = Tensors.of(x, y);
          if (Polygons.isInside(domain, px)) {
            Tensor weights = powerCoordinates.weights(domain, px);
            Tensor mean = biinvariantMean.mean(controlPointsSe2, weights);
            Tensor matrix = geodesicDisplay.matrixLift(mean);
            geometricLayer.pushMatrix(matrix);
            graphics.setColor(new Color(128, 128, 128, 64));
            graphics.fill(geometricLayer.toPath2D(geodesicDisplay.shape().multiply(RealScalar.of(.5))));
            geometricLayer.popMatrix();
          }
        }
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new PowerCoordinatesDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
