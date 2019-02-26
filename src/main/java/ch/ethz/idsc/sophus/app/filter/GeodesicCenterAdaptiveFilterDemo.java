// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Graphics2D;

import javax.swing.JSlider;
import javax.swing.JTextField;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.filter.GeodesicAdaptiveCenter;
import ch.ethz.idsc.sophus.filter.GeodesicAdaptiveCenterFilter;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicCenterAdaptiveFilterDemo extends DatasetKernelDemo {
  private Tensor refined = Tensors.empty();
  private final JSlider jSliderTime = new JSlider(1, 999, 500);
  private final JSlider jSliderPose = new JSlider(1, 999, 500);

  public GeodesicCenterAdaptiveFilterDemo() {
    JTextField textField = new JTextField("Time Interval:");
    timerFrame.jToolBar.add(textField);
    timerFrame.jToolBar.add(jSliderTime);
    JTextField textField2 = new JTextField("Pose Interval:");
    timerFrame.jToolBar.add(textField2);
    timerFrame.jToolBar.add(jSliderPose);
    updateState();
    updateStateOnly();
  }

  @Override
  protected void updateState() {
    super.updateState();
  }

  @Override
  protected void updateStateOnly() {
    super.updateStateOnly();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    TensorUnaryOperator tensorUnaryOperator = GeodesicAdaptiveCenter.of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue(), timeInterval(),
        poseInterval());
    refined = GeodesicAdaptiveCenterFilter.of(tensorUnaryOperator, spinnerRadius.getValue()).apply(control());
    return refined;
  }

  private Scalar timeInterval() {
    System.out.println("TimeInterval: +-" + RationalScalar.of(jSliderTime.getValue(), 250) + "ms");
    return RationalScalar.of(jSliderTime.getValue(), 250);
  }

  private Scalar poseInterval() {
    System.out.println("PoseInterval: +-" + RationalScalar.of(jSliderPose.getValue(), 250) + "m");
    return RationalScalar.of(jSliderPose.getValue(), 250);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicCenterAdaptiveFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
