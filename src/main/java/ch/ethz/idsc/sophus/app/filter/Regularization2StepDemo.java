// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import javax.swing.JSlider;

import ch.ethz.idsc.java.awt.BufferedImageSupplier;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.app.sym.SymGeodesic;
import ch.ethz.idsc.sophus.app.sym.SymLinkImage;
import ch.ethz.idsc.sophus.app.sym.SymLinkImages;
import ch.ethz.idsc.sophus.app.sym.SymScalar;
import ch.ethz.idsc.sophus.flt.ga.Regularization2Step;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.N;

/* package */ final class Regularization2StepDemo extends AbstractSpectrogramDemo implements BufferedImageSupplier {
  /** regularization parameter in the interval [0, 1] */
  private final JSlider jSlider = new JSlider(0, 1000, 600);

  Regularization2StepDemo() {
    super(GeodesicDisplays.SE2_R2, GokartPoseDataV2.INSTANCE);
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
    // ---
    updateState();
  }

  @Override // from AbstractDatasetFilterDemo
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    return Regularization2Step.string( //
        manifoldDisplay().geodesicInterface(), //
        N.DOUBLE.apply(sliderRatio())).apply(control());
  }

  @Override // from UniformDatasetFilterDemo
  protected String plotLabel() {
    return "Regularization2Step " + sliderRatio();
  }

  private Scalar sliderRatio() {
    return RationalScalar.of(jSlider.getValue(), jSlider.getMaximum());
  }

  @Override // from BufferedImageSupplier
  public BufferedImage bufferedImage() {
    Scalar factor = sliderRatio();
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
