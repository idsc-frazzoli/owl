// code by ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.filter.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.filter.GeodesicFIRnFilterNEW;
import ch.ethz.idsc.sophus.filter.GeodesicIIRnFilterNEW;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class GeodesicCausalFilterNEWDemo extends DatasetKernelDemo {
  private Tensor refined = Tensors.empty();
  private final JSlider jSlider = new JSlider(1, 999, 500);
  private final JToggleButton jToggleIIR = new JToggleButton("IIR");

  public GeodesicCausalFilterNEWDemo() {
    jSlider.setPreferredSize(new Dimension(500, 28));
    // ---
    jToggleIIR.setSelected(true);
    timerFrame.jToolBar.add(jToggleIIR);
    // ---
    timerFrame.jToolBar.add(jSlider);
    // ---
    updateData();
  }

  @Override
  protected void updateData() {
    super.updateData();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleSymi.isSelected())
      graphics.drawImage(SymLinkImages.causalIIR(spinnerKernel.getValue(), spinnerRadius.getValue(), alpha()).bufferedImage(), 0, 0, null);
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue());
    if (jToggleIIR.isSelected())
      refined = GeodesicIIRnFilterNEW.of(tensorUnaryOperator, geodesicDisplay().geodesicInterface(), spinnerRadius.getValue(), alpha()).apply(control());
    else
      refined = GeodesicFIRnFilterNEW.of(tensorUnaryOperator, geodesicDisplay().geodesicInterface(), spinnerRadius.getValue(), alpha()).apply(control());
    return refined;
  }

  private Scalar alpha() {
    return RationalScalar.of(jSlider.getValue(), 1000);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicCausalFilterNEWDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}