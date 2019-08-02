// code by jph
package ch.ethz.idsc.owl.math.region;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public class AffineImageRegion implements Region<Tensor> {
  private final GeometricLayer geometricLayer;
  private final int width;
  private final int height;
  private final byte[] data;
  private final boolean outside;

  /** @param bufferedImage of type BufferedImage.TYPE_BYTE_GRAY */
  public AffineImageRegion(GeometricLayer geometricLayer, BufferedImage bufferedImage, boolean outside) {
    this.geometricLayer = geometricLayer;
    width = bufferedImage.getWidth();
    height = bufferedImage.getHeight();
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    data = dataBufferByte.getData();
    this.outside = outside;
  }

  @Override
  public boolean isMember(Tensor vector) {
    Point2D point2d = geometricLayer.toPoint2D(vector);
    int x = (int) point2d.getX();
    if (0 <= x && x < width) {
      int y = (int) point2d.getY();
      if (0 <= y && y < height)
        return data[y * width + x] != 0;
    }
    return outside;
  }
}
