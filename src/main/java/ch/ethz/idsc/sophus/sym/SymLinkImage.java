// code by jph
package ch.ethz.idsc.sophus.sym;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ImageCrop;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** used in demos */
public class SymLinkImage {
  private static final int WIDTH = 100;
  /** height also appears in the model2pixel matrix */
  private static final int HEIGHT = 50;
  private static final Tensor MODEL2PIXEL = Tensors.matrix(new Number[][] { //
      { 100, 0, WIDTH / 2 }, //
      { 0, -100, HEIGHT + HEIGHT / 2 }, //
      { 0, 0, 1 } });
  private static final TensorUnaryOperator IMAGE_CROP = ImageCrop.color(Tensors.vector(255, 255, 255, 255));
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 22);
  // ---
  private final BufferedImage bufferedImage;
  private final GeometricLayer geometricLayer = GeometricLayer.of(MODEL2PIXEL);
  private int minx = Integer.MAX_VALUE;
  private int maxx = 0;

  public SymLinkImage(SymScalar symScalar) {
    this(symScalar, FONT);
  }

  public SymLinkImage(SymScalar symScalar, Font font) {
    this(symScalar, font, null);
  }

  public SymLinkImage(SymScalar symScalar, Font font, Color background) {
    final SymLink root = SymLink.build(symScalar);
    final Tensor vector = SymWeights.of(symScalar);
    final int depth = root.depth();
    Tensor position = root.getPosition();
    double max = Math.max(position.Get(0).number().doubleValue(), vector.length() - 1);
    // ---
    bufferedImage = new BufferedImage((int) (100 + Math.round(WIDTH * max)), 100 + HEIGHT * depth, BufferedImage.TYPE_INT_ARGB);
    // ---
    Graphics2D graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    graphics.setFont(font);
    final FontMetrics fontMetrics = graphics.getFontMetrics(font);
    if (Objects.nonNull(background)) {
      graphics.setColor(background);
      graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    }
    // ---
    new SymLinkRender(root).render(geometricLayer, graphics);
    // ---
    graphics.setColor(Color.GRAY);
    for (int index = 0; index < vector.length(); ++index) { // render node values
      Point2D point2d = geometricLayer.toPoint2D(Tensors.vector(index, .2));
      String string = SymLinkRender.nice(vector.Get(index), 7);
      int stringWidth = fontMetrics.stringWidth(string);
      int pix = (int) point2d.getX() - stringWidth / 2;
      minx = Math.min(minx, pix);
      maxx = Math.max(maxx, pix + stringWidth);
      graphics.drawString(string, pix, (int) point2d.getY());
    }
    { // render title bar
      graphics.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 7 }, 0));
      Point2D point2d = geometricLayer.toPoint2D(Tensors.vector(0, .13));
      graphics.drawLine(minx, (int) point2d.getY(), maxx, (int) point2d.getY());
      graphics.setStroke(new BasicStroke(1f));
    }
    { // circle bottom node
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(root.getPosition()));
      Path2D path2d = geometricLayer.toPath2D(SymLinkRender.CIRCLE_END);
      path2d.closePath();
      graphics.setColor(Color.BLACK);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
  }

  public void title(String string) {
    Graphics2D graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    Font font = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    FontMetrics fontMetrics = graphics.getFontMetrics(font);
    int stringWidth = fontMetrics.stringWidth(string);
    graphics.setFont(font);
    graphics.setColor(Color.BLACK);
    graphics.drawString(string, (int) (minx + (maxx - minx - stringWidth) * 0.5), 25);
  }

  public BufferedImage bufferedImage() {
    return bufferedImage;
  }

  public BufferedImage bufferedImageCropped(boolean line) {
    Tensor tensor = ImageFormat.from(bufferedImage);
    tensor = IMAGE_CROP.apply(tensor);
    BufferedImage image = ImageFormat.of(tensor);
    if (line) {
      Graphics2D graphics = image.createGraphics();
      GraphicsUtil.setQualityHigh(graphics);
      graphics.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 7 }, 0));
      graphics.setColor(Color.GRAY);
      int piy = 22;
      graphics.drawLine(0, piy, bufferedImage.getWidth(), piy);
    }
    return image;
  }
}
