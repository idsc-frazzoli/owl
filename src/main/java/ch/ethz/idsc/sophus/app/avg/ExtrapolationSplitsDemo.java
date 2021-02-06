// code by jph
package ch.ethz.idsc.sophus.app.avg;

import ch.ethz.idsc.sophus.app.sym.SymGeodesic;
import ch.ethz.idsc.sophus.app.sym.SymScalar;
import ch.ethz.idsc.sophus.flt.ga.GeodesicExtrapolation;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;

/** visualization of geodesic average along geodesics */
/* package */ class ExtrapolationSplitsDemo extends AbstractKernelSplitsDemo {
  public ExtrapolationSplitsDemo() {
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {2, 2, 1}, {5, 0, 2}}"));
  }

  @Override // from GeodesicAverageDemo
  SymScalar symScalar(Tensor vector) {
    ScalarUnaryOperator window = spinnerKernel.getValue().get();
    return 0 < vector.length() //
        ? (SymScalar) GeodesicExtrapolation.of(SymGeodesic.INSTANCE, window).apply(vector)
        : null;
  }

  public static void main(String[] args) {
    new ExtrapolationSplitsDemo().setVisible(1000, 600);
  }
}
