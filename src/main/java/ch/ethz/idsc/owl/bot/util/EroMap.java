// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;

/* package */ class EroMap {
  /** fine */
  // private final BufferedImage bufferedImage;
  private final int width;
  private final int height;
  private final double rad;
  private final double wid;
  private final byte[] data;
  private final Graphics2D graphics;
  // private final Tensor matrix;
  private final GeometricLayer geometricLayer;
  // ---
  private final BufferedImage erodedImage;

  /** @param bufferedImage
   * @param matrix
   * @param radius of erosion */
  public EroMap(BufferedImage bufferedImage, Tensor matrix, int radius) {
    // this.bufferedImage = bufferedImage;
    width = bufferedImage.getWidth();
    height = bufferedImage.getHeight();
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    data = dataBufferByte.getData();
    graphics = bufferedImage.createGraphics();
    // this.matrix = matrix;
    geometricLayer = GeometricLayer.of(Inverse.of(matrix));
    // ---
    erodedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    rad = radius + 0.5;
    wid = rad * 2;
  }

  public void setPixel(Tensor tensor, boolean occupy) {
    Point2D point2d = geometricLayer.toPoint2D(tensor);
    graphics.setColor(occupy ? Color.WHITE : Color.BLACK);
    graphics.fill(new Rectangle2D.Double(point2d.getX(), point2d.getY(), 1, 1));
  }

  public BufferedImage updateErodedMap() {
    Graphics2D graphics2d = erodedImage.createGraphics();
    graphics2d.setColor(Color.BLACK);
    graphics2d.fillRect(0, 0, width, height);
    graphics2d.setColor(Color.WHITE);
    int index = 0;
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        if (data[index] != 0)
          graphics2d.fill(new Ellipse2D.Double(x - rad, y - rad, wid, wid));
        ++index;
      }
    }
    return erodedImage;
  }
}
