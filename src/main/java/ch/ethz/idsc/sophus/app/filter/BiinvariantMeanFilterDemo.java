// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.filter.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class BiinvariantMeanFilterDemo extends DatasetKernelDemo {
  public BiinvariantMeanFilterDemo() {
    updateState();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    TensorUnaryOperator tensorUnaryOperator = //
        BiinvariantMeanCenter.of(geodesicDisplay.biinvariantMean(), spinnerKernel.getValue());
    return GeodesicCenterFilter.of(tensorUnaryOperator, spinnerRadius.getValue()).apply(control());
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new BiinvariantMeanFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
