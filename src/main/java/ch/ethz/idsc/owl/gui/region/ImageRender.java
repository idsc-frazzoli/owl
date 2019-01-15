// code by jph
package ch.ethz.idsc.owl.gui.region;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

public class ImageRender implements RenderInterface {
  /** @param bufferedImage
   * @param range */
  public static ImageRender of(BufferedImage bufferedImage, Tensor range) {
    Tensor scale = Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()) //
        .pmul(range.map(Scalar::reciprocal));
    return new ImageRender(bufferedImage, scale);
  }

  // ---
  private final BufferedImage bufferedImage;
  private final Tensor matrix;

  /** @param bufferedImage
   * @param scale */
  public ImageRender(BufferedImage bufferedImage, Tensor scale) {
    this.bufferedImage = bufferedImage;
    Tensor invsc = DiagonalMatrix.of( //
        +scale.Get(0).reciprocal().number().doubleValue(), //
        -scale.Get(1).reciprocal().number().doubleValue(), 1);
    Tensor translate = Se2Utils.toSE2Translation( //
        Tensors.vector(0, -bufferedImage.getHeight()));
    matrix = invsc.dot(translate);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.drawImage(bufferedImage, //
        AffineTransforms.toAffineTransform(geometricLayer.getMatrix().dot(matrix)), null);
  }
}
