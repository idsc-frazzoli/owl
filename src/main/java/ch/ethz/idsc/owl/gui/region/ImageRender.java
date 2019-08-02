// code by jph
package ch.ethz.idsc.owl.gui.region;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** does not render boundaries correctly for all BufferedImage types */
public class ImageRender implements RenderInterface {
  /** @param bufferedImage
   * @param range vector of length 2, i.e. the extensions of the image in model coordinates */
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

  // TODO JPH OWL 049 only 1 constructor
  public ImageRender(BufferedImage bufferedImage, Tensor matrix, boolean some) {
    this.bufferedImage = bufferedImage;
    this.matrix = matrix.copy();
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(matrix);
    graphics.drawImage(bufferedImage, //
        AffineTransforms.toAffineTransform(geometricLayer.getMatrix()), null);
    graphics.setColor(Color.RED);
    Path2D path2d = geometricLayer.toPath2D(Tensors.of( //
        Tensors.vector(0, 0), //
        Tensors.vector(bufferedImage.getWidth(), 0), //
        Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()), //
        Tensors.vector(0, bufferedImage.getHeight())), true);
    graphics.draw(path2d);
    geometricLayer.popMatrix();
  }
}
