// code by jph
package ch.ethz.idsc.sophus.app.avg;

import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.filter.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.sym.SymGeodesic;
import ch.ethz.idsc.sophus.sym.SymScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** visualization of geodesic average along geodesics */
public class ExtrapolationSplitsDemo extends KernelSplitsDemo {
  public ExtrapolationSplitsDemo() {
    setControl(Tensors.fromString("{{0, 0, 0}, {2, 2, 1}, {5, 0, 2}}"));
  }

  @Override // from GeodesicAverageDemo
  SymScalar symScalar(Tensor vector) {
    return (SymScalar) GeodesicExtrapolation.of(SymGeodesic.INSTANCE, spinnerKernel.getValue()).apply(vector);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new ExtrapolationSplitsDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
