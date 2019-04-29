// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Graphics2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.filter.NonuniformGeodesicCenterFilterNEW;
import ch.ethz.idsc.sophus.filter.NonuniformGeodesicCenterNEW;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class NonuniformGeodesicCenterFilterDemoNEW extends StateTimeDatasetKernelDemoNEW {
  private Tensor refined = Tensors.empty();
  protected final JToggleButton jToggleFixedRadius = new JToggleButton("fixedRadius");

  public NonuniformGeodesicCenterFilterDemoNEW() {
    jToggleFixedRadius.setSelected(false);
    timerFrame.jToolBar.add(jToggleFixedRadius);
    // ---
    updateStateTime();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // interval manuel gekoppelt an sampling frequency
    Scalar interval = RationalScalar.of(spinnerRadius.getValue(), 19);
    if (jToggleFixedRadius.isSelected())
      interval = RealScalar.of(spinnerRadius.getValue()).negate();
    NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW = NonuniformGeodesicCenterNEW.of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue());
    // TODO JPH Frage: gibt es eine effizientere Methode zur conversion von collection to tensor?
    refined = Tensor.of(NonuniformGeodesicCenterFilterNEW.of(nonuniformGeodesicCenterNEW, interval).apply(navigableMapStateTime()).values().stream());
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new NonuniformGeodesicCenterFilterDemoNEW();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
