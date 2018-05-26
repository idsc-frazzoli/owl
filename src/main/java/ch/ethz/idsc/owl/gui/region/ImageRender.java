// code by jph
package ch.ethz.idsc.owl.gui.region;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class ImageRender implements RenderInterface {
  private final BufferedImage bufferedImage;
  private final Tensor invsc;

  /** @param bufferedImage
   * @param scale */
  // TODO document parameters
  public ImageRender(BufferedImage bufferedImage, Tensor scale) {
    this.bufferedImage = bufferedImage;
    invsc = DiagonalMatrix.of( //
        +scale.Get(0).reciprocal().number().doubleValue(), //
        -scale.Get(1).reciprocal().number().doubleValue(), 1);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor model2pixel = geometricLayer.getMatrix();
    Tensor translate = IdentityMatrix.of(3);
    translate.set(RealScalar.of(-bufferedImage.getHeight()), 1, 2);
    Tensor matrix = model2pixel.dot(invsc).dot(translate);
    graphics.drawImage(bufferedImage, AffineTransforms.toAffineTransform(matrix), null);
  }
}
