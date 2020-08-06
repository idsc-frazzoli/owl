// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class H2WeightingDemo extends A2ScatteredSetCoordinateDemo {
  public H2WeightingDemo() {
    super(true, GeodesicDisplays.H2_ONLY, LogWeightings.list());
    setControlPointsSe2(Tensors.fromString( //
        "{{-1.900, 1.783, 0.000}, {-0.083, 2.517, 0.000}, {0.500, 1.400, 0.000}, {2.300, 2.117, 0.000}, {2.833, 0.217, 0.000}, {1.000, -1.550, 0.000}, {-0.283, -0.667, 0.000}, {-1.450, -1.650, 0.000}}"));
    recompute();
  }

  public static void main(String[] args) {
    new H2WeightingDemo().setVisible(1200, 900);
  }
}
