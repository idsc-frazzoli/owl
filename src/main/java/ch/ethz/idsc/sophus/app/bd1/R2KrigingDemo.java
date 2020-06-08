// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class R2KrigingDemo extends A2KrigingDemo {
  public R2KrigingDemo() {
    super(GeodesicDisplays.R2_H2);
    timerFrame.configCoordinateOffset(400, 400);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 1}, {3, 0, 1}, {-3, 1, 0}, {-2, -3, 0}, {1, 3, 0}}"));
  }

  public static void main(String[] args) {
    new R2KrigingDemo().setVisible(1300, 800);
  }
}
