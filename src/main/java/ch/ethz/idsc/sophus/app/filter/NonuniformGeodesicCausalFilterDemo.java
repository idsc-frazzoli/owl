// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.filter.NonuniformGeodesicCausalFilter;
import ch.ethz.idsc.sophus.filter.NonuniformGeodesicExtrapolation;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class NonuniformGeodesicCausalFilterDemo extends StateTimeDatasetKernelDemo {
  private Tensor refined = Tensors.empty();

  public NonuniformGeodesicCausalFilterDemo() {
    updateStateTime();
  }

  @Override
  protected void updateStateTime() {
    super.updateStateTime();
    Scalar interval = RationalScalar.of(spinnerRadius.getValue(), 10);
    // TODO OB is Se2Geodesic.INSTANCE sufficiently generic here?
    TensorUnaryOperator tensorUnaryOperator = NonuniformGeodesicExtrapolation.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN);
    refined = Tensor.of(NonuniformGeodesicCausalFilter.of(tensorUnaryOperator, interval).apply(controlStateTime()).stream().map(st -> st.extract(1, 4)));
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new NonuniformGeodesicCausalFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}