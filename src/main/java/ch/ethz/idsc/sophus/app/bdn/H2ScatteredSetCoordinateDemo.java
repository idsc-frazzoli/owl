// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.H2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class H2ScatteredSetCoordinateDemo extends A2ScatteredSetCoordinateDemo {
  public H2ScatteredSetCoordinateDemo() {
    super(true, GeodesicDisplays.H2_ONLY, LogWeightings.list());
    setControlPointsSe2(Tensors.fromString("{{-0.51, 0.32, 0}, {0.33, 0.54, 0}, {-0.45, -0.36, 0}, {0.27, -0.38, -1}}"));
    setMidpointIndicated(false);
  }

  @Override
  public Tensor compute(WeightingInterface weightingInterface, int refinement) {
    Tensor sX = Subdivide.of(-3.0, +3.0, refinement);
    Tensor sY = Subdivide.of(+3.0, -3.0, refinement);
    int n = sX.length();
    final Tensor origin = getGeodesicControlPoints();
    Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, n, n, origin.length());
    IntStream.range(0, n).parallel().forEach(c0 -> {
      Scalar x = sX.Get(c0);
      int c1 = 0;
      for (Tensor y : sY) {
        Tensor point = H2GeodesicDisplay.INSTANCE.project(Tensors.of(x, y));
        wgs.set(weightingInterface.weights(origin, point), c1, c0);
        ++c1;
      }
    });
    return wgs;
  }

  public static void main(String[] args) {
    new H2ScatteredSetCoordinateDemo().setVisible(1200, 900);
  }
}
