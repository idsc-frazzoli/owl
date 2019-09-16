// code by jph
package ch.ethz.idsc.sophus.app.avg;

import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.flt.ga.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.sym.SymGeodesic;
import ch.ethz.idsc.sophus.sym.SymScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** visualization of geodesic average along geodesics */
public class ExtrapolationSplitsDemo extends KernelSplitsDemo {
  public ExtrapolationSplitsDemo() {
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {2, 2, 1}, {5, 0, 2}}"));
  }

  @Override // from GeodesicAverageDemo
  SymScalar symScalar(Tensor vector) {
    return (SymScalar) GeodesicExtrapolation.of(SymGeodesic.INSTANCE, spinnerKernel.getValue()).apply(vector);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new ExtrapolationSplitsDemo();
    abstractDemo.setVisible(1000, 600);
  }
}
