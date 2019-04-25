// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.filter.NonuniformGeodesicCenter;
import ch.ethz.idsc.sophus.filter.NonuniformGeodesicCenterFilter;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class NonuniformGeodesicCenterFilterDemo extends StateTimeDatasetKernelDemo {
  private Tensor refined = Tensors.empty();

  public NonuniformGeodesicCenterFilterDemo() {
    updateStateTime();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Scalar interval = RationalScalar.of(spinnerRadius.getValue(), 10);
    TensorUnaryOperator tensorUnaryOperator = NonuniformGeodesicCenter.of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue());
    refined = NonuniformGeodesicCenterFilter.of(tensorUnaryOperator, interval).apply(controlStateTime());
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new NonuniformGeodesicCenterFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
