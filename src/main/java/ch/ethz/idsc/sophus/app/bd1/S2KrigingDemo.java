// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Max;

/* package */ class S2KrigingDemo extends A2KrigingDemo {
  public S2KrigingDemo() {
    super(GeodesicDisplays.S2_RP2);
    setControlPointsSe2(Tensors.fromString("{{0.01, 0, 1}, {0.2, 0, 0}, {0.21, 0.2, 0}, {0, 0.2, 0}}"));
    // ---
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    // ---
    timerFrame.configCoordinateOffset(400, 400);
  }

  @Override
  void prepare() {
    Tensor pointsSe2 = getControlPointsSe2().copy();
    pointsSe2.set(Max.function(RealScalar.ZERO), Tensor.ALL, 2);
    setControlPointsSe2(pointsSe2);
  }

  public static void main(String[] args) {
    new S2KrigingDemo().setVisible(1300, 800);
  }
}
