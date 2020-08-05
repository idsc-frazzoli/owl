// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import ch.ethz.idsc.sophus.app.api.LogWeightings;

/* package */ class H2WeightingDemo extends H2ScatteredSetCoordinateDemo {
  public H2WeightingDemo() {
    super(LogWeightings.list());
  }

  public static void main(String[] args) {
    new H2WeightingDemo().setVisible(1200, 900);
  }
}
