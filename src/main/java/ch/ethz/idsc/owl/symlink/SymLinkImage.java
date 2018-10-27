// code by jph
package ch.ethz.idsc.owl.symlink;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ImageCrop;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

class SymLinkImage {
  private static final Tensor MODEL2PIXEL = Tensors.fromString("{{100,0,80},{0,-100,50+25},{0,0,1}}");
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 22);
  private static final TensorUnaryOperator IMAGE_CROP = ImageCrop.color(Tensors.vector(255, 255, 255, 255));
  // private static final Tensor CIRCLE = CirclePoints.of(21).multiply(RealScalar.of(.07));
  // ---
  private final BufferedImage bufferedImage = new BufferedImage(1400, 500, BufferedImage.TYPE_INT_ARGB);
  private final GeometricLayer geometricLayer = GeometricLayer.of(MODEL2PIXEL);
  int minx = 800;
  int maxx = 0;

  public SymLinkImage(SymScalar symScalar) {
    Graphics2D graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    graphics.setFont(FONT);
    FontMetrics fontMetrics = graphics.getFontMetrics(FONT);
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    // ---
    SymLink root = SymLink.build(symScalar);
    new SymLinkRender(root).render(geometricLayer, graphics);
    // ---
    Tensor vector = SymWeights.of(symScalar);
    graphics.setColor(Color.GRAY);
    for (int index = 0; index < vector.length(); ++index) {
      Point2D point2d = geometricLayer.toPoint2D(Tensors.vector(index, .2));
      String string = SymLinkRender.nice(vector.Get(index));
      int stringWidth = fontMetrics.stringWidth(string);
      int pix = (int) point2d.getX() - stringWidth / 2;
      minx = Math.min(minx, pix);
      maxx = Math.max(maxx, pix + stringWidth);
      graphics.drawString(string, pix, (int) point2d.getY());
    }
    {
      graphics.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 7 }, 0));
      Point2D point2d = geometricLayer.toPoint2D(Tensors.vector(0, .13));
      graphics.drawLine(minx, (int) point2d.getY(), maxx, (int) point2d.getY());
      graphics.setStroke(new BasicStroke(1f));
    }
    {
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
    FontMetrics fontMetrics = graphics.getFontMetrics(FONT);
    int stringWidth = fontMetrics.stringWidth(string);
    graphics.setFont(FONT);
    graphics.setColor(Color.BLACK);
    graphics.drawString(string, (int) (minx + (maxx - minx - stringWidth) * 0.5), 25);
  }

  public BufferedImage bufferedImage() {
    return bufferedImage;
  }

  public BufferedImage bufferedImageCropped() {
    Tensor tensor = ImageFormat.from(bufferedImage);
    tensor = IMAGE_CROP.apply(tensor);
    BufferedImage image = ImageFormat.of(tensor);
    // Graphics2D graphics = image.createGraphics();
    // GraphicsUtil.setQualityHigh(graphics);
    // graphics.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 7 }, 0));
    // graphics.setColor(Color.GRAY);
    // int piy = 22;
    // graphics.drawLine(0, piy, bufferedImage.getWidth(), piy);
    return image;
  }
}
