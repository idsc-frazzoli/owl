// code by jph /ob
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

public class GeodesicCenterFilterDemo extends DatasetKernelDemo {
  private Tensor refined = Tensors.empty();

  public GeodesicCenterFilterDemo() {
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
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue());
    refined = control();
    for (int index = 0; index < spinnerConvolution.getValue(); index++) {
      refined = GeodesicCenterFilter.of(tensorUnaryOperator, spinnerRadius.getValue()).apply(refined);
    }
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
