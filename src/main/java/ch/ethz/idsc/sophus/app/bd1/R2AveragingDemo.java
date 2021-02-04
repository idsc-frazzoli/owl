// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class R2AveragingDemo extends A2AveragingDemo {
  public R2AveragingDemo() {
    super(GeodesicDisplays.R2_H2);
    timerFrame.geometricComponent.setOffset(400, 400);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 1}, {1, 0, 1}, {-1, 1, 0}, {-0.5, -1, 0}, {0.4, 1, 0}}"));
  }

  public static void main(String[] args) {
    new R2AveragingDemo().setVisible(1300, 800);
  }
}
