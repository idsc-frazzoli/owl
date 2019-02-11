// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.filter.GeodesicExtrapolate;
import ch.ethz.idsc.sophus.filter.GeodesicExtrapolateFilter;
import ch.ethz.idsc.sophus.math.WindowSideSampler;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class GeodesicExtrapolateDemo extends DatasetKernelDemo {
  private Tensor refined = Tensors.empty();

  public GeodesicExtrapolateDemo() {
    updateData();
    // ---
  }

  @Override
  protected void updateData() {
    super.updateData();
    // ---
    WindowSideSampler windowSideSampler = new WindowSideSampler(spinnerKernel.getValue());
    TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolate.of(geodesicDisplay().geodesicInterface(), windowSideSampler);
    refined = GeodesicExtrapolateFilter.of(tensorUnaryOperator, spinnerRadius.getValue()).apply(control());
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleSymi.isSelected())
      graphics.drawImage(SymLinkImages.Extrapolate(spinnerKernel.getValue(), spinnerRadius.getValue()).bufferedImage(), 0, 0, null);
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicExtrapolateDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}