// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.filter.NonuniformFixedIntervalGeodesicCenter;
import ch.ethz.idsc.sophus.filter.NonuniformFixedIntervalGeodesicCenterFilter;
import ch.ethz.idsc.sophus.filter.NonuniformFixedRadiusGeodesicCenter;
import ch.ethz.idsc.sophus.filter.NonuniformFixedRadiusGeodesicCenterFilter;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class NonuniformGeodesicCenterFilterDemo extends NavigableMapDatasetKernelDemo {
  private Tensor refined = Tensors.empty();
  protected final JToggleButton jToggleFixedRadius = new JToggleButton("fixedRadius");
  // interval manuel gekoppelt an sampling frequency
  protected final long samplingFrequency = 20;

  public NonuniformGeodesicCenterFilterDemo() {
    jToggleFixedRadius.setSelected(true);
    timerFrame.jToolBar.add(jToggleFixedRadius);
    setGeodesicDisplay(Se2GeodesicDisplay.INSTANCE);
    // ---
    updateStateTime();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // interval is either: radius length or muliplicity of sampling frequency - depending on filter choice
    if (jToggleFixedRadius.isSelected()) {
      NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = NonuniformFixedRadiusGeodesicCenter.of(geodesicDisplay().geodesicInterface());
      refined = Tensor.of(NonuniformFixedRadiusGeodesicCenterFilter.of(nonuniformFixedRadiusGeodesicCenter, spinnerRadius.getValue())
          .apply(navigableMapStateTime()).values().stream());
    } else {
      NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter = NonuniformFixedIntervalGeodesicCenter
          .of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue());
      refined = Tensor.of(
          NonuniformFixedIntervalGeodesicCenterFilter.of(nonuniformFixedIntervalGeodesicCenter, RationalScalar.of(spinnerRadius.getValue(), samplingFrequency))
              .apply(navigableMapStateTime()).values().stream());
    }
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new NonuniformGeodesicCenterFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }

  @Override
  protected BufferedImage symLinkImage() {
    return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
  }
}
