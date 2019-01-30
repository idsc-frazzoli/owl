// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.filter.GeodesicFIRnFilter;
import ch.ethz.idsc.sophus.filter.GeodesicIIRnFilter;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowSideSampler;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class GeodesicCausalFilterDemo extends DatasetKernelDemo {
  private static final Tensor SQUARE = //
      Tensors.vector(index -> Tensors.vector(index * 0.01, 100 < index && index < 200 ? 1 : 0, 0), 300).unmodifiable();
  // ---
  private final JToggleButton jToggleStep = new JToggleButton("step");
  private final JToggleButton jToggleIIR = new JToggleButton("IIR");
  private final JSlider jSlider = new JSlider(1, 999, 500);

  GeodesicCausalFilterDemo() {
    jToggleStep.setSelected(false);
    timerFrame.jToolBar.add(jToggleStep);
    // ---
    jToggleIIR.setSelected(true);
    timerFrame.jToolBar.add(jToggleIIR);
    // ---
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
  }

  @Override
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleStep.isSelected())
      _control = Tensor.of(SQUARE.stream().map(geodesicDisplay()::project));
    // ---
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    SmoothingKernel smoothingKernel = spinnerFilter.getValue();
    int radius = spinnerRadius.getValue();
    WindowSideSampler windowSideSampler = new WindowSideSampler(smoothingKernel);
    Tensor mask = windowSideSampler.apply(radius);
    mask.append(alpha());
    TensorUnaryOperator geodesicCenterFilter;
    if (jToggleIIR.isSelected())
      geodesicCenterFilter = new GeodesicIIRnFilter(geodesicDisplay.geodesicInterface(), mask);
    else
      geodesicCenterFilter = new GeodesicFIRnFilter(geodesicDisplay.geodesicInterface(), mask);
    return Tensor.of(control().stream().map(geodesicCenterFilter));
  }

  @Override
  protected String plotLabel() {
    return super.plotLabel() + " " + alpha();
  }

  private Scalar alpha() {
    return RationalScalar.of(jSlider.getValue(), 1000);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicCausalFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1400, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
