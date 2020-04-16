// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.H2GeodesicDisplay;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class H2KrigingDemo extends D2KrigingDemo {
  public H2KrigingDemo() {
    super(H2GeodesicDisplay.INSTANCE);
    timerFrame.configCoordinateOffset(400, 400);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 1}, {3, 0, 1}, {-3, 1, 0}, {-2, -3, 0}, {1, 3, 0}}"));
  }

  @Override
  double rad() {
    return 5;
  }

  public static void main(String[] args) {
    new H2KrigingDemo().setVisible(1000, 800);
  }
}
