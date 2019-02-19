// code by ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.filter.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.filter.GeodesicFIRnFilter;
import ch.ethz.idsc.sophus.filter.GeodesicIIRnFilter;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class GeodesicCausalFilterDemo extends DatasetKernelDemo {
  private Tensor refined = Tensors.empty();
  /** IIR vs. FIR filter type */
  private final JToggleButton jToggleIIR = new JToggleButton("IIR");
  /** parameter to blend extrapolation with measurement */
  private final JSlider jSlider = new JSlider(1, 999, 500);

  public GeodesicCausalFilterDemo() {
    jSlider.setPreferredSize(new Dimension(500, 28));
    // ---
    jToggleIIR.setSelected(true);
    timerFrame.jToolBar.add(jToggleIIR);
    // ---
    timerFrame.jToolBar.add(jSlider);
    // ---
    updateData();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // TODO OB: adapt symLinkImages to new filter structure
    // if (jToggleSymi.isSelected())
    // graphics.drawImage(SymLinkImages.causalIIR(spinnerKernel.getValue(), spinnerRadius.getValue(), alpha()).bufferedImage(), 0, 0, null);
    GeodesicInterface geodesicInterface = geodesicDisplay().geodesicInterface();
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(geodesicInterface, spinnerKernel.getValue());
    refined = jToggleIIR.isSelected() //
        ? GeodesicIIRnFilter.of(tensorUnaryOperator, geodesicInterface, spinnerRadius.getValue(), alpha()).apply(control())
        : GeodesicFIRnFilter.of(tensorUnaryOperator, geodesicInterface, spinnerRadius.getValue(), alpha()).apply(control());
    return refined;
  }

  private Scalar alpha() {
    return RationalScalar.of(jSlider.getValue(), 1000);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicCausalFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}