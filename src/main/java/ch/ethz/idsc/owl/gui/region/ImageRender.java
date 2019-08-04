// code by jph
package ch.ethz.idsc.owl.gui.region;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.sca.N;

/** Hint:
 * On ubuntu, we have observed for grayscale images that the initial rendering
 * configuration influences the rendering when rotating the image. */
public class ImageRender implements RenderInterface {
  private static final Color COLOR = new Color(0, 0, 255, 32);

  /** @param bufferedImage
   * @param pixel2model with dimensions 3 x 3
   * @return */
  public static ImageRender of(BufferedImage bufferedImage, Tensor pixel2model) {
    return new ImageRender(bufferedImage, pixel2model);
  }

  /** @param bufferedImage
   * @param range vector of length 2, i.e. the extensions of the image in model coordinates */
  public static ImageRender range(BufferedImage bufferedImage, Tensor range) {
    Tensor scale = Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()) //
        .pmul(range.map(Scalar::reciprocal));
    return scale(bufferedImage, scale);
  }

  /** @param bufferedImage
   * @param scale vector of length 2 */
  public static ImageRender scale(BufferedImage bufferedImage, Tensor scale) {
    VectorQ.requireLength(scale, 2);
    return new ImageRender(bufferedImage, Dot.of( //
        DiagonalMatrix.with(scale.map(Scalar::reciprocal).append(RealScalar.ONE)), //
        Se2Matrix.flipY(bufferedImage.getHeight())));
  }

  // ---
  private final BufferedImage bufferedImage;
  private final Tensor pixel2model;
  private final Tensor box;

  private ImageRender(BufferedImage bufferedImage, Tensor pixel2model) {
    this.bufferedImage = bufferedImage;
    this.pixel2model = MatrixQ.requireSize(pixel2model, 3, 3).copy();
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();
    box = N.DOUBLE.of(Tensors.of( //
        Tensors.vector(0, 0), //
        Tensors.vector(width, 0), //
        Tensors.vector(width, height), //
        Tensors.vector(0, height)));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(pixel2model);
    graphics.drawImage(bufferedImage, AffineTransforms.toAffineTransform(geometricLayer.getMatrix()), null);
    graphics.setColor(COLOR);
    graphics.draw(geometricLayer.toPath2D(box, true));
    geometricLayer.popMatrix();
  }
}
