// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import javax.swing.JSlider;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.GokartPoseDataV2;
import ch.ethz.idsc.sophus.app.util.BufferedImageSupplier;
import ch.ethz.idsc.sophus.flt.ga.Regularization2Step;
import ch.ethz.idsc.sophus.sym.SymGeodesic;
import ch.ethz.idsc.sophus.sym.SymLinkImage;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.sophus.sym.SymScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.N;

/* package */ class Regularization2StepDemo extends UniformDatasetFilterDemo implements BufferedImageSupplier {
  /** regularization parameter in the interval [0, 1] */
  private final JSlider jSlider = new JSlider(0, 1000, 600);

  Regularization2StepDemo() {
    super(GeodesicDisplays.SE2_R2, GokartPoseDataV2.INSTANCE);
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
    // ---
    updateState();
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final GeodesicDisplay geodesicDisplay = geodesicDisplay();
    final Scalar factor = factor();
    // ---
    return Regularization2Step.string(geodesicDisplay.geodesicInterface(), N.DOUBLE.apply(factor)).apply(control());
  }

  @Override // from DatasetFilterDemo
  protected String plotLabel() {
    return "Regularization2Step " + factor();
  }

  private Scalar factor() {
    return RationalScalar.of(jSlider.getValue(), jSlider.getMaximum());
  }

  @Override // from BufferedImageSupplier
  public BufferedImage bufferedImage() {
    Scalar factor = factor();
    TensorUnaryOperator tensorUnaryOperator = Regularization2Step.string(SymGeodesic.INSTANCE, factor);
    Tensor vector = Tensor.of(IntStream.range(0, 3).mapToObj(SymScalar::leaf));
    Tensor tensor = tensorUnaryOperator.apply(vector);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor.get(1), SymLinkImages.FONT_SMALL);
    symLinkImage.title("Regularization2Step [" + factor + "]");
    return symLinkImage.bufferedImage();
  }

  public static void main(String[] args) {
    new Regularization2StepDemo().setVisible(1200, 600);
  }
}
