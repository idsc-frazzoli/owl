// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Graphics2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.filter.NonuniformFixedIntervalGeodesicCenterFilterNEW;
import ch.ethz.idsc.sophus.filter.NonuniformFixedIntervalGeodesicCenterNEW;
import ch.ethz.idsc.sophus.filter.NonuniformFixedRadiusGeodesicCenterFilterNEW;
import ch.ethz.idsc.sophus.filter.NonuniformFixedRadiusGeodesicCenterNEW;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class NonuniformGeodesicCenterFilterDemoNEW extends StateTimeDatasetKernelDemoNEW {
  private Tensor refined = Tensors.empty();
  protected final JToggleButton jToggleFixedRadius = new JToggleButton("fixedRadius");
  // interval manuel gekoppelt an sampling frequency
  protected Scalar samplingFrequency = RealScalar.of(20);

  public NonuniformGeodesicCenterFilterDemoNEW() {
    jToggleFixedRadius.setSelected(true);
    timerFrame.jToolBar.add(jToggleFixedRadius);
    // ---
    updateStateTime();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // interval is either: radius length or muliplicity of sampling frequency - depending on filter choice
    Scalar interval = RealScalar.of(spinnerRadius.getValue());
    if (jToggleFixedRadius.isSelected()) {
      NonuniformFixedRadiusGeodesicCenterNEW nonuniformFixedRadiusGeodesicCenterNEW = NonuniformFixedRadiusGeodesicCenterNEW
          .of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue());
      refined = Tensor.of(
          NonuniformFixedRadiusGeodesicCenterFilterNEW.of(nonuniformFixedRadiusGeodesicCenterNEW, interval).apply(navigableMapStateTime()).values().stream());
    } else {
      NonuniformFixedIntervalGeodesicCenterNEW nonuniformFixedIntervalGeodesicCenterNEW = NonuniformFixedIntervalGeodesicCenterNEW
          .of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue());
      refined = Tensor.of(NonuniformFixedIntervalGeodesicCenterFilterNEW.of(nonuniformFixedIntervalGeodesicCenterNEW, interval.divide(samplingFrequency))
          .apply(navigableMapStateTime()).values().stream());
    }
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new NonuniformGeodesicCenterFilterDemoNEW();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
