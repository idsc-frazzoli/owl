// code by jph
package ch.ethz.idsc.owl.tensor.usr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;

enum DemoHelper {
  ;
  static final Tensor SE2 = Tensors.fromString("{{180, 0, 6}, {0, -180, 186}, {0, 0, 1}}").unmodifiable();
  static final Tensor POINT = CirclePoints.of(10).multiply(RealScalar.of(.015)).unmodifiable();

  static BufferedImage createWhite() {
    BufferedImage bufferedImage = new BufferedImage(192, 192, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, 192, 192);
    return bufferedImage;
  }
}
