// code by jph
package ch.ethz.idsc.owl.gui.region;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;

public class ImageRender implements RenderInterface {
  /** @param bufferedImage
   * @param range vector of length 2 */
  public static ImageRender of(BufferedImage bufferedImage, Tensor range) {
    Tensor scale = Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()) //
        .pmul(range.map(Scalar::reciprocal));
    return new ImageRender(bufferedImage, scale);
  }

  // ---
  private final BufferedImage bufferedImage;
  private final Tensor matrix;

  /** @param bufferedImage
   * @param scale vector of length 2 */
  public ImageRender(BufferedImage bufferedImage, Tensor scale) {
    this.bufferedImage = bufferedImage;
    VectorQ.requireLength(scale, 2);
    Tensor weights = Tensors.of(scale.Get(0).reciprocal(), scale.Get(1).reciprocal().negate(), RealScalar.ONE);
    Tensor translate = Se2Utils.toSE2Translation(Tensors.vector(0, -bufferedImage.getHeight()));
    matrix = weights.pmul(translate);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.drawImage(bufferedImage, //
        AffineTransforms.toAffineTransform(geometricLayer.getMatrix().dot(matrix)), null);
  }
}
