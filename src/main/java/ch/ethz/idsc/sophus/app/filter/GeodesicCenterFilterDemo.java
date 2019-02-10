// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class GeodesicCenterFilterDemo extends DatasetKernelDemo {
  private Tensor refined = Tensors.empty();

  public GeodesicCenterFilterDemo() {
    updateData();
  }

  @Override
  protected void updateData() {
    super.updateData();
    // ---
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue());
    refined = GeodesicCenterFilter.of(tensorUnaryOperator, spinnerRadius.getValue()).apply(control());
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleSymi.isSelected())
      graphics.drawImage(SymLinkImages.geodesicCenter(spinnerKernel.getValue(), spinnerRadius.getValue()).bufferedImage(), 0, 0, null);
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicCenterFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
