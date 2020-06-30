// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.Rp2GeodesicDisplay;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Max;

/* package */ class S2KrigingDemo extends A2KrigingDemo {
  public S2KrigingDemo() {
    super(GeodesicDisplays.S2_RP2);
    // setControlPointsSe2(Tensors.fromString("{{0.01, 0, 1}, {0.2, 0, 0}, {0.21, 0.2, 0}, {0, 0.2, 0}}"));
    // ---
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    // ---
    timerFrame.configCoordinateOffset(400, 400);
    setGeodesicDisplay(Rp2GeodesicDisplay.INSTANCE);
    setControlPointsSe2(Tensors.fromString( //
        "{{59/150, 1/2, 0.7853981633974483}, {-47/75, 13/300, 1.0471975511965976}, {37/300, 11/15, 0}, {-13/75, 119/300, 0}}"));
    setControlPointsSe2(Tensors.fromString( //
        "{{-1/25, -7/25, 0}, {-8/25, 77/150, 0.7853981633974483}, {-13/25, -17/25, 0}, {-79/100, 11/75, 0}}"));
  }

  @Override
  void prepare() {
    Tensor pointsSe2 = getControlPointsSe2().copy();
    pointsSe2.set(Max.function(RealScalar.ZERO), Tensor.ALL, 2);
    setControlPointsSe2(pointsSe2);
  }

  public static void main(String[] args) {
    new S2KrigingDemo().setVisible(1400, 800);
  }
}
