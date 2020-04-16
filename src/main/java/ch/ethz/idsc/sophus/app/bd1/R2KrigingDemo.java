// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class R2KrigingDemo extends D2KrigingDemo {
  public R2KrigingDemo() {
    super(R2GeodesicDisplay.INSTANCE);
    timerFrame.configCoordinateOffset(400, 400);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0.01, 0, 1}, {2, 0, 0}, {-3, -1, 0}, {0, 1, 0}}"));
  }

  @Override
  double rad() {
    return 5;
  }

  public static void main(String[] args) {
    new R2KrigingDemo().setVisible(1000, 800);
  }
}
