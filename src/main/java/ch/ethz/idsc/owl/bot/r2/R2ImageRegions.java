// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.ImageFormat;

/** collection of ready-to-use image regions */
public enum R2ImageRegions {
  ;
  static Region<Tensor> normal(BufferedImage bufferedImage, Tensor range, boolean strict) {
    return ImageRegion.of(bufferedImage, range, strict);
  }

  // the use of normal is preferred over transpose
  static ImageRegion transpose(BufferedImage bufferedImage, Tensor range, boolean strict) {
    return new ImageRegion(Transpose.of(ImageFormat.from(bufferedImage)), range, strict);
  }

  public static Region<Tensor> outside_0b36(Tensor range) {
    CharImage charImage = CharImage.fillBlack(new Dimension(256, 256));
    charImage.setFont(new Font(Font.DIALOG, Font.PLAIN, 256));
    charImage.draw('\u0b36', new Point(30, 200));
    return normal(charImage.bufferedImage(), range, false);
  }

  public static BufferedImage inside_0b36() {
    CharImage charImage = CharImage.fillWhite(new Dimension(210, 256));
    charImage.draw('\u0b36', new Point(0, 240));
    return charImage.bufferedImage();
  }

  public static BufferedImage inside_265b() {
    CharImage charImage = CharImage.fillWhite(new Dimension(320, 320));
    charImage.draw('\u265b', new Point(-20, 300));
    return charImage.bufferedImage();
  }

  public static BufferedImage inside_2180() {
    CharImage charImage = CharImage.fillWhite(new Dimension(480, 320));
    charImage.setFont(new Font(Font.DIALOG, Font.BOLD, 385));
    charImage.draw('\u2180', new Point(-10, 300));
    return charImage.bufferedImage();
  }

  public static BufferedImage inside_2181() {
    CharImage charImage = CharImage.fillWhite(new Dimension(300, 320));
    charImage.setFont(new Font(Font.DIALOG, Font.BOLD, 385));
    charImage.draw('\u2181', new Point(-10, 300));
    return charImage.bufferedImage();
  }

  public static BufferedImage inside_2182() {
    CharImage charImage = CharImage.fillWhite(new Dimension(480, 320));
    charImage.draw('\u2182', new Point(-10, 305));
    return charImage.bufferedImage();
  }

  public static ImageRegion inside_0f5c() {
    CharImage charImage = CharImage.fillWhite(new Dimension(320, 640));
    charImage.setFont(new Font(Font.DIALOG, Font.PLAIN, 600));
    charImage.draw('\u0f5c', new Point(20, 560));
    return transpose(charImage.bufferedImage(), Tensors.vector(20, 10), false);
  }

  /***************************************************/
  public static final R2ImageRegionWrap _2180 = //
      new R2ImageRegionWrap(inside_2180(), Tensors.vector(10, 7), 15);
  public static final R2ImageRegionWrap _2181 = //
      new R2ImageRegionWrap(inside_2181(), Tensors.vector(10, 7), 15);
  /***************************************************/
  public static final R2ImageRegionWrap _0F5C_2182 = //
      new R2ImageRegionWrap(ImageFormat.of(inside_0f5c_2182_charImage()), Tensors.vector(20, 10), 15);

  public static Tensor inside_0f5c_2182_charImage() {
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

  private static BufferedImage inside_gtob_charImage() {
    CharImage charImage = CharImage.fillWhite(new Dimension(640, 640));
    charImage.setFont(new Font(Font.DIALOG, Font.BOLD, 400));
    charImage.draw('G', new Point(0, 310));
    charImage.draw('T', new Point(280, 323));
    charImage.draw('I', new Point(480, 323));
    charImage.draw('O', new Point(20, 560));
    charImage.draw('B', new Point(280, 580));
    return ImageFormat.of(Transpose.of(ImageFormat.from(charImage.bufferedImage())));
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
}
