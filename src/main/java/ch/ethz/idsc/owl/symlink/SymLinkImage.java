package ch.ethz.idsc.owl.symlink;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ImageFormat;

public class SymLinkImage {
  private static final Tensor MODEL2PIXEL = Tensors.fromString("{{100,0,50},{0,-100,80},{0,0,1}}");
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 20);
  // ---
  private final BufferedImage bufferedImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
  private final GeometricLayer geometricLayer = GeometricLayer.of(MODEL2PIXEL);
  private final Graphics2D graphics = bufferedImage.createGraphics();
  private final FontMetrics fontMetrics;

  public SymLinkImage() {
    GraphicsUtil.setQualityHigh(graphics);
    graphics.setFont(FONT);
    fontMetrics = graphics.getFontMetrics(FONT);
  }

  public void renderRoot(SymLink root) {
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    // ---
    new SymLinkRender(root).render(geometricLayer, graphics);
  }

  public void renderTops(Tensor vector) {
    graphics.setColor(Color.GRAY);
    Point2D point2d = null;
    for (int index = 0; index < vector.length(); ++index) {
      point2d = geometricLayer.toPoint2D(Tensors.vector(index, .3));
      String string = SymLinkRender.nice(vector.Get(index));
      int stringWidth = fontMetrics.stringWidth(string);
      graphics.drawString(string, (int) point2d.getX() - stringWidth / 2, (int) point2d.getY());
    }
    Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0);
    graphics.setStroke(dashed);
    graphics.drawLine(50, 60, (int) point2d.getX(), 60);
  }

  public BufferedImage bufferedImage() {
    Tensor tensor = ImageFormat.from(bufferedImage);
    tensor = ImageCrop.of(tensor, Tensors.vector(255, 255, 255, 255));
    System.out.println(Dimensions.of(tensor));
    return ImageFormat.of(tensor);
  }
}
