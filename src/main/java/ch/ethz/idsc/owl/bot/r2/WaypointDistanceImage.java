// code by ynager
package ch.ethz.idsc.owl.bot.r2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ImageFormat;

/* package */ class WaypointDistanceImage {
  public static final int OFF_PATH_COST = 1;
  // ---
  private final BufferedImage bufferedImage;
  private final Scalar model2pixel;

  /** connects given waypoints with linear strokes
   * 
   * @param waypoints matrix with at least 2 columns
   * @param closed
   * @param width of path in model space
   * @param model2pixel factor
   * @param dimension {width, height} of the image
   * @return grayscale image */
  public WaypointDistanceImage( //
      Tensor waypoints, boolean closed, Scalar width, Scalar model2pixel, Dimension dimension) {
    bufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_BYTE_GRAY);
    this.model2pixel = model2pixel;
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(new Color(OFF_PATH_COST, OFF_PATH_COST, OFF_PATH_COST));
    graphics.fillRect(0, 0, dimension.width, dimension.height);
    graphics.setColor(Color.BLACK);
    BasicStroke basicStroke = new BasicStroke( //
        width.multiply(model2pixel).number().floatValue(), //
        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    graphics.setStroke(basicStroke);
    Path2D path = GeometricLayer.of(Tensors.matrix(new Scalar[][] { //
        { model2pixel, RealScalar.ZERO, RealScalar.ZERO }, //
        { RealScalar.ZERO, model2pixel.negate(), RealScalar.of(dimension.height - 1) }, //
        { RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE } })).toPath2D(waypoints);
    if (closed)
      path.closePath();
    graphics.draw(path);
  }

  public BufferedImage bufferedImage() {
    return bufferedImage;
  }

  public Tensor image() {
    return ImageFormat.from(bufferedImage);
  }

  public Tensor range() {
    return Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()).divide(model2pixel);
  }
}
