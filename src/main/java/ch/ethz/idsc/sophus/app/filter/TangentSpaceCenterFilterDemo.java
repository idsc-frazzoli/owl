// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.filter.TangentSpaceCenter;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;

public class TangentSpaceCenterFilterDemo extends DatasetKernelDemo {
  private final SpinnerLabel<Integer> spinnerConvolution = new SpinnerLabel<>();
  private Tensor refined = Tensors.empty();
  final JToggleButton jToggleTS = new JToggleButton("TangentSpace");

  public TangentSpaceCenterFilterDemo() {
    {
      spinnerConvolution.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerConvolution.setIndex(0);
      spinnerConvolution.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "convolution");
      spinnerConvolution.addSpinnerListener(type -> updateState());
    }
    jToggleTS.setSelected(false);
    timerFrame.jToolBar.add(jToggleTS);
    // ---
    updateState();
  }

  @Override
  protected void updateState() {
    super.updateState();
    // ---
    // try {
    // _control = DuckietownPositions.states(Import.of(HomeDirectory.file("duckiebot_0_poses.csv")));
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleTS.isSelected()) {
      TensorUnaryOperator tensorUnaryOperator = TangentSpaceCenter.of( //
          geodesicDisplay().lieGroup(), geodesicDisplay().lieExponential(), spinnerKernel.getValue());
      refined = Nest.of( //
          GeodesicCenterFilter.of(tensorUnaryOperator, spinnerRadius.getValue()), //
          control(), spinnerConvolution.getValue());
    } else {
      TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue());
      refined = Nest.of( //
          GeodesicCenterFilter.of(tensorUnaryOperator, spinnerRadius.getValue()), //
          control(), spinnerConvolution.getValue());
    }
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new TangentSpaceCenterFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
