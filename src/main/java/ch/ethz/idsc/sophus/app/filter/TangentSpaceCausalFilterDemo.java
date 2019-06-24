// code by ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.function.Function;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.crv.spline.MonomialExtrapolationMask;
import ch.ethz.idsc.sophus.filter.WindowSideExtrapolation;
import ch.ethz.idsc.sophus.filter.ts.TangentSpaceFIRnFilter;
import ch.ethz.idsc.sophus.filter.ts.TangentSpaceIIRnFilter;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO OB adapt symLinkImages to new filter structure, see use of BufferedImageSupplier
/* package */ class TangentSpaceCausalFilterDemo extends DatasetKernelDemo {
  /** IIR vs. FIR filter type */
  private final JToggleButton jToggleIIR = new JToggleButton("IIR");
  private final JToggleButton jToggleMon = new JToggleButton("Mon");
  /** parameter to blend extrapolation with measurement */
  private final JSlider jSlider = new JSlider(1, 999, 500);

  public TangentSpaceCausalFilterDemo() {
    super(GeodesicDisplays.SE2_R2);
    // ---
    jSlider.setPreferredSize(new Dimension(500, 28));
    // ---
    jToggleIIR.setSelected(true);
    timerFrame.jToolBar.add(jToggleIIR);
    // ---
    timerFrame.jToolBar.add(jToggleMon);
    // ---
    timerFrame.jToolBar.add(jSlider);
    // ---
    updateState();
  }

  @Override
  protected void updateState() {
    super.updateState();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    LieExponential lieExponential = geodesicDisplay.lieExponential();
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    final int radius = spinnerRadius.getValue();
    if (0 < radius) {
      TensorUnaryOperator tensorUnaryOperator;
      Function<Integer, Tensor> function = jToggleMon.isSelected() //
          ? MonomialExtrapolationMask.INSTANCE
          : WindowSideExtrapolation.of(spinnerKernel.getValue());
      if (jToggleIIR.isSelected()) {
        tensorUnaryOperator = TangentSpaceIIRnFilter.of( //
            lieGroup, lieExponential, function, geodesicInterface, spinnerRadius.getValue(), alpha());
      } else {
        tensorUnaryOperator = TangentSpaceFIRnFilter.of( //
            lieGroup, lieExponential, function, geodesicInterface, spinnerRadius.getValue(), alpha());
      }
      return tensorUnaryOperator.apply(control());
    }
    return control();
  }

  private Scalar alpha() {
    return RationalScalar.of(jSlider.getValue(), 1000);
  }

  @Override
  protected String plotLabel() {
    return super.plotLabel() + " " + alpha();
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new TangentSpaceCausalFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}