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
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.sca.N;

/** Hint:
 * On ubuntu, we have observed for grayscale images that the initial rendering
 * configuration influences the rendering when rotating the image. */
public class ImageRender implements RenderInterface {
  /** @param bufferedImage
   * @param matrix with dimensions 3 x 3
   * @return */
  public static ImageRender of(BufferedImage bufferedImage, Tensor matrix) {
    return new ImageRender(bufferedImage, matrix);
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
    return new ImageRender(bufferedImage, //
        DiagonalMatrix.with(scale.map(Scalar::reciprocal).append(RealScalar.ONE)) //
            .dot(Se2Matrix.flipY(bufferedImage.getHeight())));
  }

  // ---
  private final BufferedImage bufferedImage;
  private final Tensor box;
  private final Tensor matrix;

  private ImageRender(BufferedImage bufferedImage, Tensor matrix) {
    this.bufferedImage = bufferedImage;
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();
    box = N.DOUBLE.of(Tensors.of( //
        Tensors.vector(0, 0), //
        Tensors.vector(width, 0), //
        Tensors.vector(width, height), //
        Tensors.vector(0, height)));
    this.matrix = MatrixQ.requireSize(matrix, 3, 3).copy();
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(matrix);
    graphics.drawImage(bufferedImage, //
        AffineTransforms.toAffineTransform(geometricLayer.getMatrix()), null);
    graphics.setColor(new Color(255, 0, 0, 128));
    graphics.draw(geometricLayer.toPath2D(box, true));
    geometricLayer.popMatrix();
  }
}
