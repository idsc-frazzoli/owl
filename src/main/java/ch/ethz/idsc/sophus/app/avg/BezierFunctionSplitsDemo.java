// code by jph
package ch.ethz.idsc.sophus.app.avg;

import java.awt.Dimension;

import javax.swing.JSlider;

import ch.ethz.idsc.sophus.app.sym.SymGeodesic;
import ch.ethz.idsc.sophus.app.sym.SymScalar;
import ch.ethz.idsc.sophus.crv.bezier.BezierFunction;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;

/** visualization of geodesic average along geodesics */
/* package */ class BezierFunctionSplitsDemo extends AbstractSplitsDemo {
  private final JSlider jSlider = new JSlider(0, 1000, 500);

  public BezierFunctionSplitsDemo() {
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {2, 2, 1}, {5, 0, 2}}"));
  }

  @Override // from GeodesicAverageDemo
  SymScalar symScalar(Tensor vector) {
    int n = vector.length();
    if (0 < n) {
      ScalarTensorFunction scalarTensorFunction = BezierFunction.of(SymGeodesic.INSTANCE, vector);
      Scalar parameter = n <= 1 //
          ? RealScalar.ZERO
          : RationalScalar.of(n, n - 1);
      parameter = parameter.multiply(RationalScalar.of(jSlider.getValue(), 1000));
      return (SymScalar) scalarTensorFunction.apply(parameter);
    }
    return null;
  }

  public static void main(String[] args) {
    new BezierFunctionSplitsDemo().setVisible(1000, 600);
  }
}
