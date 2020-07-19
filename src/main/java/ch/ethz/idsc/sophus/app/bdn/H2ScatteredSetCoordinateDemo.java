// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.H2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class H2ScatteredSetCoordinateDemo extends A2ScatteredSetCoordinateDemo {
  private static final double RANGE = 3;

  public H2ScatteredSetCoordinateDemo() {
    super(true, GeodesicDisplays.H2_ONLY, LogWeightings.list());
    setControlPointsSe2(Tensors.fromString( //
        "{{-1.900, 1.783, 0.000}, {-0.083, 2.517, 0.000}, {0.500, 1.400, 0.000}, {2.300, 2.117, 0.000}, {2.833, 0.217, 0.000}, {1.000, -1.550, 0.000}, {-0.283, -0.667, 0.000}, {-1.450, -1.650, 0.000}}"));
    recompute();
  }

  @Override
  public Tensor compute(TensorUnaryOperator tensorUnaryOperator, int refinement) {
    Tensor sX = Subdivide.of(-RANGE, +RANGE, refinement);
    Tensor sY = Subdivide.of(+RANGE, -RANGE, refinement);
    int n = sX.length();
    Tensor origin = getGeodesicControlPoints();
    Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, n, n, origin.length());
    IntStream.range(0, n).parallel().forEach(c0 -> {
      Scalar x = sX.Get(c0);
      int c1 = 0;
      for (Tensor y : sY) {
        Tensor point = H2GeodesicDisplay.INSTANCE.project(Tensors.of(x, y));
        wgs.set(tensorUnaryOperator.apply(point), c1, c0);
        ++c1;
      }
    });
    return wgs;
  }

  public static void main(String[] args) {
    new H2ScatteredSetCoordinateDemo().setVisible(1200, 900);
  }
}
