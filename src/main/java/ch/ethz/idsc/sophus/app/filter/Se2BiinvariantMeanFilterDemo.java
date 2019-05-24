// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.filter.Se2BiinvariantMeanFilter;
import ch.ethz.idsc.sophus.filter.Se2BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class Se2BiinvariantMeanFilterDemo extends DatasetKernelDemo {
  private Tensor refined = Tensors.empty();

  public Se2BiinvariantMeanFilterDemo() {
    // ---
    updateState();
  }

  @Override
  protected void updateState() {
    super.updateState();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    TensorUnaryOperator tensorUnaryOperator = Se2BiinvariantMeanCenter.of(SmoothingKernel.GAUSSIAN);
    refined = Se2BiinvariantMeanFilter.of(tensorUnaryOperator, 4).apply(control());
    return refined;
  }

  public static void main(String[] args) {
//    AbstractDemo abstractDemo = new Se2BiinvariantMeanFilterDemo();
//    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
//    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
