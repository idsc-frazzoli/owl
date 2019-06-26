// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JSlider;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.util.BufferedImageSupplier;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicAdaptiveCenter;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;

public class GeodesicAdaptiveCenterFilterDemo extends DatasetKernelDemo implements BufferedImageSupplier {
  private final SpinnerLabel<Integer> spinnerConvolution = new SpinnerLabel<>();
  private Tensor refined = Tensors.empty();
  private final JSlider jSlider = new JSlider(1, 999, 500);

  public GeodesicAdaptiveCenterFilterDemo() {
    {
      spinnerConvolution.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerConvolution.setIndex(0);
      spinnerConvolution.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "convolution");
      spinnerConvolution.addSpinnerListener(type -> updateState());
    }
    // ---
    timerFrame.jToolBar.add(jSlider);
    // ---
    setGeodesicDisplay(Se2GeodesicDisplay.INSTANCE);
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
    TensorUnaryOperator tensorUnaryOperator = GeodesicAdaptiveCenter.of(geodesicDisplay().geodesicInterface(), spinnerKernel.getValue(),
        RationalScalar.of(jSlider.getValue(), 100));
    refined = Nest.of( //
        CenterFilter.of(tensorUnaryOperator, spinnerRadius.getValue()), //
        control(), spinnerConvolution.getValue());
    return refined;
  }

  @Override // from BufferedImageSupplier
  public BufferedImage bufferedImage() {
    return GeodesicCenterSymLinkImage.of(spinnerKernel.getValue(), spinnerRadius.getValue()).bufferedImage();
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicAdaptiveCenterFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
