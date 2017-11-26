// code by jph
package ch.ethz.idsc.owl.gui.region;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.AffineTransforms;
import ch.ethz.idsc.owl.gui.GeometricLayer;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class ImageRegionRender implements RenderInterface {
  private final BufferedImage bufferedImage;
  private final Tensor invsc;

  public ImageRegionRender(ImageRegion imageRegion) {
    bufferedImage = RegionRenders.image(imageRegion.image());
    Tensor scale = imageRegion.scale();
    invsc = DiagonalMatrix.of( //
        scale.Get(0).reciprocal().number().doubleValue(), //
        -scale.Get(1).reciprocal().number().doubleValue(), 1);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor model2pixel = geometricLayer.getMatrix();
    Tensor translate = IdentityMatrix.of(3);
    translate.set(RealScalar.of(-bufferedImage.getHeight()), 1, 2);
    Tensor matrix = model2pixel.dot(invsc).dot(translate);
    graphics.drawImage(bufferedImage, AffineTransforms.toAffineTransform(matrix), null);
  }
}
