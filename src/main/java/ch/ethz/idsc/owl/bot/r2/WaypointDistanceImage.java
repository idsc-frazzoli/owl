// code by ynager
package ch.ethz.idsc.owl.bot.r2;

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

/** TODO JPH document and test */
public enum WaypointDistanceImage {
  ;
  public static final int OFF_PATH_COST = 1;

  /** connects given waypoints with linear strokes
   * 
   * @param waypoints matrix with at least 2 columns
   * @param range vector of length 2 with entries in model space
   * @param pathWidth in pixels
   * @param resolution {width, height} of the image
   * @return grayscale image */
  public static BufferedImage linear(Tensor waypoints, Tensor range, float pathWidth, Dimension resolution) {
    BufferedImage bufferedImage = new BufferedImage(resolution.width, resolution.height, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(new Color(OFF_PATH_COST, OFF_PATH_COST, OFF_PATH_COST));
    graphics.fillRect(0, 0, resolution.width, resolution.height);
    graphics.setColor(Color.BLACK);
    graphics.setStroke(new BasicStroke(pathWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    Path2D path = path2D(waypoints, range, resolution);
    path.closePath();
    graphics.draw(path);
    return bufferedImage;
  }

  public static Path2D path2D(Tensor waypoints, Tensor range, Dimension resolution) {
    int max_y = resolution.height - 1;
    Tensor scale = Tensors.vector(resolution.height, resolution.width).pmul(range.map(Scalar::reciprocal));
    float scaleX = scale.Get(1).number().floatValue();
    float scaleY = scale.Get(0).number().floatValue();
    Tensor model2pixel = Tensors.matrix(new Number[][] { //
        { scaleX, 0, 0 }, { 0, -scaleY, max_y }, { 0, 0, 1 } });
    GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
    return geometricLayer.toPath2D(waypoints);
  }
}
