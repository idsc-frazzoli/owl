// code by jph
package ch.ethz.idsc.sophus.app.avg;

import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.sym.SymGeodesic;
import ch.ethz.idsc.sophus.sym.SymScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class GeodesicCenterSplitsDemo extends KernelSplitsDemo {
  public GeodesicCenterSplitsDemo() {
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {2, 2, 1}, {5, 0, 2}}"));
  }

  @Override
  SymScalar symScalar(Tensor vector) {
    if (vector.length() % 2 == 1)
      return (SymScalar) GeodesicCenter.of(SymGeodesic.INSTANCE, spinnerKernel.getValue()).apply(vector);
    return null;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicCenterSplitsDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
