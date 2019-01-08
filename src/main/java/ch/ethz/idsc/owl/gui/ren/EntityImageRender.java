// code by ynager
package ch.ethz.idsc.owl.gui.ren;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/** Renders an arbitrary image at the supplier state */
public class EntityImageRender implements RenderInterface {
  private final Tensor matrix;
  private final Supplier<StateTime> supplier;
  private BufferedImage img;

  public EntityImageRender(Supplier<StateTime> supplier, BufferedImage img, Tensor range) {
    this.supplier = supplier;
    this.img = img;
    Tensor scale = Tensors.vector(img.getWidth(), img.getHeight()) //
        .pmul(range.map(Scalar::reciprocal));
    Tensor invsc = DiagonalMatrix.of( //
        +scale.Get(0).reciprocal().number().doubleValue(), //
        -scale.Get(1).reciprocal().number().doubleValue(), 1);
    Tensor translate = Se2Utils.toSE2Translation( //
        Tensors.vector(-img.getWidth() / 3, -img.getHeight() / 2));
    matrix = invsc.dot(translate);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(supplier.get().state()));
    AffineTransform trans = AffineTransforms.toAffineTransform(geometricLayer.getMatrix().dot(matrix));
    graphics.drawImage(img, trans, null);
    geometricLayer.popMatrix();
  }
}
