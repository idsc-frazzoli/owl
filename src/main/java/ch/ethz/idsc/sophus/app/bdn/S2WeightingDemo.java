package ch.ethz.idsc.sophus.app.bdn;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;

/* package */ class S2WeightingDemo extends S2ScatteredSetCoordinateDemo {
  public S2WeightingDemo() {
    super(GeodesicDisplays.S2_ONLY, LogWeightings.list());
  }

  public static void main(String[] args) {
    new S2WeightingDemo().setVisible(1300, 900);
  }
}
