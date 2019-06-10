// code by jph, ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.BufferedImageSupplier;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.sym.SymGeodesic;
import ch.ethz.idsc.sophus.sym.SymLinkImage;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.sophus.sym.SymScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;

// TODO JPH demo obsolete once sym link image is supported by LieGroupFiltersDemo
public class GeodesicCenterFilterDemo extends DatasetKernelDemo implements BufferedImageSupplier {
  private final SpinnerLabel<Integer> spinnerConvolution = new SpinnerLabel<>();
  private Tensor refined = Tensors.empty();

  public GeodesicCenterFilterDemo() {
    {
      spinnerConvolution.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerConvolution.setIndex(0);
      spinnerConvolution.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "convolution");
      spinnerConvolution.addSpinnerListener(type -> updateState());
    }
    // ---
    updateState();
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    // geodesic center
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(geodesicDisplay.geodesicInterface(), spinnerKernel.getValue());
    refined = Nest.of( //
        GeodesicCenterFilter.of(tensorUnaryOperator, spinnerRadius.getValue()), //
        control(), spinnerConvolution.getValue());
    return refined;
  }

  @Override // from BufferedImageSupplier
  public BufferedImage bufferedImage() {
    return symLinkImage(spinnerKernel.getValue(), spinnerRadius.getValue()).bufferedImage();
  }

  public static SymLinkImage symLinkImage(SmoothingKernel smoothingKernel, int radius) {
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(SymGeodesic.INSTANCE, smoothingKernel);
    Tensor vector = Tensor.of(IntStream.range(0, 2 * radius + 1).mapToObj(SymScalar::leaf));
    Tensor tensor = tensorUnaryOperator.apply(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor, SymLinkImages.FONT_SMALL);
    symLinkImage.title(smoothingKernel.name() + "[" + (2 * radius + 1) + "]");
    return symLinkImage;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicCenterFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
