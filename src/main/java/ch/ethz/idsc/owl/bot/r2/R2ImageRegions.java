// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.ImageFormat;

/** collection of ready-to-use image regions */
public enum R2ImageRegions {
  ;
  static ImageRegion normal(BufferedImage bufferedImage, Tensor range, boolean strict) {
    return new ImageRegion(ImageFormat.from(bufferedImage), range, strict);
  }

  // the use of normal is preferred over transpose
  static ImageRegion transpose(BufferedImage bufferedImage, Tensor range, boolean strict) {
    return new ImageRegion(Transpose.of(ImageFormat.from(bufferedImage)), range, strict);
  }

  public static ImageRegion outside_0b36() {
    CharImage charImage = CharImage.fillBlack(new Dimension(256, 256));
    charImage.setFont(new Font(Font.DIALOG, Font.PLAIN, 256));
    charImage.draw('\u0b36', new Point(30, 200));
    return normal(charImage.bufferedImage(), Tensors.vector(7, 7), false);
  }

  public static ImageRegion inside_0b36() {
    CharImage charImage = CharImage.fillWhite(new Dimension(210, 256));
    charImage.draw('\u0b36', new Point(0, 240));
    return normal(charImage.bufferedImage(), Tensors.vector(6, 7), false);
  }

  public static ImageRegion inside_265b() {
    CharImage charImage = CharImage.fillWhite(new Dimension(320, 320));
    charImage.draw('\u265b', new Point(-20, 300));
    return normal(charImage.bufferedImage(), Tensors.vector(7, 7), false);
  }

  public static ImageRegion inside_2180() {
    CharImage charImage = CharImage.fillWhite(new Dimension(480, 320));
    charImage.setFont(new Font(Font.DIALOG, Font.BOLD, 385));
    charImage.draw('\u2180', new Point(-10, 300));
    return normal(charImage.bufferedImage(), Tensors.vector(9, 6), false);
  }

  public static ImageRegion inside_2181() {
    CharImage charImage = CharImage.fillWhite(new Dimension(300, 320));
    charImage.setFont(new Font(Font.DIALOG, Font.BOLD, 385));
    charImage.draw('\u2181', new Point(-10, 300));
    return normal(charImage.bufferedImage(), Tensors.vector(6, 6), false);
  }

  public static ImageRegion inside_2182() {
    CharImage charImage = CharImage.fillWhite(new Dimension(480, 320));
    charImage.draw('\u2182', new Point(-10, 305));
    return normal(charImage.bufferedImage(), Tensors.vector(9, 6), false);
  }

  public static ImageRegion inside_0f5c() {
    CharImage charImage = CharImage.fillWhite(new Dimension(320, 640));
    charImage.setFont(new Font(Font.DIALOG, Font.PLAIN, 600));
    charImage.draw('\u0f5c', new Point(20, 560));
    return transpose(charImage.bufferedImage(), Tensors.vector(20, 10), false);
  }

  /***************************************************/
  public static final R2ImageRegionWrap _2180 = //
      new R2ImageRegionWrap(inside_2180().image(), Tensors.vector(10, 7), 15);
  public static final R2ImageRegionWrap _2181 = //
      new R2ImageRegionWrap(inside_2181().image(), Tensors.vector(10, 7), 15);
  /***************************************************/
  public static final R2ImageRegionWrap _0F5C_2182 = //
      new R2ImageRegionWrap(inside_0f5c_2182_charImage(), Tensors.vector(20, 10), 15);

  private static Tensor inside_0f5c_2182_charImage() {
    CharImage charImage = CharImage.fillWhite(new Dimension(320, 640));
    charImage.setFont(new Font(Font.DIALOG, Font.PLAIN, 600));
    charImage.draw('\u0f5c', new Point(20, 560));
    charImage.setFont(new Font(Font.DIALOG, Font.PLAIN, 270));
    charImage.draw('\u2182', new Point(-5, 230));
    charImage.draw('\u2182', new Point(-5, 420));
    return Transpose.of(ImageFormat.from(charImage.bufferedImage()));
  }

  /***************************************************/
  public static final R2ImageRegionWrap _GTOB = //
      new R2ImageRegionWrap(inside_gtob_charImage(), Tensors.vector(12, 12), 15);

  private static Tensor inside_gtob_charImage() {
    CharImage charImage = CharImage.fillWhite(new Dimension(640, 640));
    charImage.setFont(new Font(Font.DIALOG, Font.BOLD, 400));
    charImage.draw('G', new Point(0, 310));
    charImage.draw('T', new Point(280, 323));
    charImage.draw('I', new Point(480, 323));
    charImage.draw('O', new Point(20, 560));
    charImage.draw('B', new Point(280, 580));
    return Transpose.of(ImageFormat.from(charImage.bufferedImage()));
  }

  /***************************************************/
  public static CharImage inside_roundabout() {
    CharImage charImage = CharImage.fillWhite(new Dimension(236, 180));
    charImage.setFont(new Font(Font.DIALOG, Font.BOLD, 400));
    charImage.draw('a', new Point(-20, 200));
    return charImage;
  }

  private static final Tensor CIRC_RANGE = Tensors.vector(3, 4);

  public static ImageRegion inside_circ() {
    CharImage charImage = inside_roundabout();
    return transpose(charImage.bufferedImage(), CIRC_RANGE, false);
  }

  /***************************************************/
  /** Creates an ImageRegion where the obstacle free space is generated by linearly interpolating
   * waypoints, and dilating the resulting path to achieve the specified pathWidth
   * 
   * @param tensor of waypoints of which only the first two dimensions are considered
   * @param pathWidth
   * @param dimension
   * @param range vector of length 2
   * @return */
  // TODO resolution and range not yet decoupled. possibly use GeometricLayer
  // TODO option for cyclic waypoints: connect last with first
  public static R2ImageRegionWrap fromWaypoints(Tensor waypoints, float pathWidth, Dimension dimension, Tensor range) {
    BufferedImage bufferedImage = new BufferedImage( //
        dimension.width, //
        dimension.height, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = bufferedImage.createGraphics();
    // GeometricLayer geometricLayer = new GeometricLayer(model2pixel, Array.zeros(3));
    final Rectangle2D rInit = new Rectangle2D.Double(0, 0, dimension.width, dimension.height);
    final Area rInitArea = new Area(rInit);
    final Stroke stroke = new BasicStroke(pathWidth, BasicStroke.CAP_ROUND, BasicStroke.CAP_BUTT);
    for (int i = 0; i < waypoints.length() - 1; i++) {
      Line2D line = new Line2D.Double( //
          toImagePoint(waypoints.get(i + 0), dimension.height), //
          toImagePoint(waypoints.get(i + 1), dimension.height));
      Area lineArea = new Area(stroke.createStrokedShape(line));
      if (lineArea.isEmpty())
        System.err.print("empty");
      rInitArea.subtract(lineArea);
    }
    graphics.fill(rInitArea);
    return new R2ImageRegionWrap(ImageFormat.from(bufferedImage), range, 30);
  }

  private static Point2D toImagePoint(Tensor waypoint, int height) {
    return new Point2D.Double( //
        waypoint.Get(0).number().doubleValue(), //
        height - waypoint.Get(1).number().doubleValue() - 1);
  }
}
