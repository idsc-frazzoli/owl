// code by ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.filter.TangentSpaceFIRnFilter;
import ch.ethz.idsc.sophus.filter.TangentSpaceIIRnFilter;
import ch.ethz.idsc.sophus.group.Se2BiinvariantMean;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

// TODO OB adapt symLinkImages to new filter structure, see use of BufferedImageSupplier
/* package */ class Se2TangentSpaceCausalFilterDemo extends DatasetKernelDemo {
  private Tensor refined = Tensors.empty();
  /** IIR vs. FIR filter type */
  private final JToggleButton jToggleIIR = new JToggleButton("IIR");
  private final SpinnerLabel<Se2BiinvariantMean> spinnerFilters = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerConvolution = new SpinnerLabel<>();
  /** parameter to blend extrapolation with measurement */
  private final JSlider jSlider = new JSlider(1, 999, 500);

  public Se2TangentSpaceCausalFilterDemo() {
    super(GeodesicDisplays.SE2_ONLY);
    {
      spinnerFilters.setArray(Se2BiinvariantMean.values());
      spinnerFilters.setIndex(0);
      spinnerFilters.addToComponentReduced(timerFrame.jToolBar, new Dimension(90, 28), "se2 biinvariant mean");
      spinnerFilters.addSpinnerListener(type -> updateState());
    }
    {
      spinnerConvolution.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerConvolution.setIndex(0);
      spinnerConvolution.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "convolution");
      spinnerConvolution.addSpinnerListener(type -> updateState());
    }
    // ---
    jSlider.setPreferredSize(new Dimension(500, 28));
    // ---
    jToggleIIR.setSelected(true);
    timerFrame.jToolBar.add(jToggleIIR);
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
    final int radius = spinnerRadius.getValue();
    if (0 < radius) {
      if (jToggleIIR.isSelected()) {
        refined = TangentSpaceIIRnFilter.of(spinnerKernel.getValue(), spinnerRadius.getValue(), alpha()).apply(control());
      } else {
        refined = TangentSpaceFIRnFilter.of(spinnerKernel.getValue(), spinnerRadius.getValue(), alpha()).apply(control());
      }
      return refined;
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
    AbstractDemo abstractDemo = new Se2TangentSpaceCausalFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}