// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum WaypointDistanceImages {
  ;
  static final int OFF_PATH_COST = 1;

  /** @param waypoints
   * @param range vector of length 2 with entries in model space
   * @param pathWidth in pixels
   * @param resolution {width, height} */
  public static BufferedImage linear(Tensor waypoints, Tensor range, float pathWidth, Dimension resolution) {
    int max_y = resolution.height - 1;
    Tensor scale = Tensors.vector(resolution.height, resolution.width).pmul(range.map(Scalar::reciprocal));
    float scaleX = scale.Get(1).number().floatValue();
    float scaleY = scale.Get(0).number().floatValue();
    BufferedImage bufferedImage = new BufferedImage(resolution.width, resolution.height, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(new Color(OFF_PATH_COST, OFF_PATH_COST, OFF_PATH_COST));
    graphics.fillRect(0, 0, resolution.width, resolution.height);
    graphics.setColor(new Color(0, 0, 0));
    graphics.setStroke(new BasicStroke(pathWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    Tensor model2pixel = Tensors.matrix(new Number[][] { //
        { scaleX, 0, 0 }, { 0, -scaleY, max_y }, { 0, 0, 1 } });
    GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
    Path2D path = geometricLayer.toPath2D(waypoints);
    path.closePath();
    graphics.draw(path);
    return bufferedImage;
  }
}
