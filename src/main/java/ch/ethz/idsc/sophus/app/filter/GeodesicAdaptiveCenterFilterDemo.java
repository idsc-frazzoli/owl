// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Graphics2D;

import javax.swing.JSlider;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.filter.GeodesicAdaptiveCenter;
import ch.ethz.idsc.sophus.filter.GeodesicAdaptiveCenterFilter;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicAdaptiveCenterFilterDemo extends DatasetKernelDemo {
  private Tensor refined = Tensors.empty();
  private final JSlider jSlider = new JSlider(1, 999, 500);

  public GeodesicAdaptiveCenterFilterDemo() {
    // ---
    timerFrame.jToolBar.add(jSlider);
    updateState();
  }

  @Override
  protected void updateState() {
    super.updateState();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    TensorUnaryOperator tensorUnaryOperator = GeodesicAdaptiveCenter.of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue(), interval());
    refined = GeodesicAdaptiveCenterFilter.of(tensorUnaryOperator, spinnerRadius.getValue()).apply(control());
    return refined;
  }

  private Scalar interval() {
    return RationalScalar.of(jSlider.getValue(), 250);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicAdaptiveCenterFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
