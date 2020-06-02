// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.Optional;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class S2ScatteredSetCoordinateDemo extends A2ScatteredSetCoordinateDemo {
  private final JToggleButton jToggleLower = new JToggleButton("lower");

  public S2ScatteredSetCoordinateDemo() {
    super(true, GeodesicDisplays.S2_ONLY, LogWeightings.list());
    {
      jToggleLower.setSelected(true);
      timerFrame.jToolBar.add(jToggleLower);
    }
    setControlPointsSe2(Tensors.fromString("{{-0.51, 0.32, 0}, {0.33, 0.54, 0}, {-0.45, -0.36, 0}, {0.27, -0.38, -1}}"));
    setMidpointIndicated(false);
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(500, 500);
  }

  @Override
  public Tensor compute(WeightingInterface weightingInterface, int refinement) {
    Tensor sX = Subdivide.of(-1.0, +1.0, refinement);
    Tensor sY = Subdivide.of(+1.0, -1.0, refinement);
    int n = sX.length();
    boolean lower = jToggleLower.isSelected();
    final Tensor origin = getGeodesicControlPoints();
    Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, lower ? n * 2 : n, n, origin.length());
    IntStream.range(0, n).parallel().forEach(c0 -> {
      Scalar x = sX.Get(c0);
      int c1 = 0;
      for (Tensor y : sY) {
        Optional<Tensor> optionalP = S2GeodesicDisplay.optionalZ(Tensors.of(x, y, RealScalar.ONE));
        if (optionalP.isPresent()) {
          Tensor point = optionalP.get();
          wgs.set(weightingInterface.weights(origin, point), c1, c0);
          if (lower) {
            point.set(Scalar::negate, 2);
            wgs.set(weightingInterface.weights(origin, point), n + c1, c0);
          }
        }
        ++c1;
      }
    });
    return wgs;
  }

  public static void main(String[] args) {
    new S2ScatteredSetCoordinateDemo().setVisible(1200, 900);
  }
}
