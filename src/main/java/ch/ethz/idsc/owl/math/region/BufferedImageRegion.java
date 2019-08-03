// code by jph
package ch.ethz.idsc.owl.math.region;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;

public class BufferedImageRegion implements Region<Tensor>, RenderInterface {
  private final BufferedImage bufferedImage;
  private final AffinePoint2D affinePoint2D;
  private final Tensor matrix;
  private final int width;
  private final int height;
  private final byte[] data;
  private final boolean outside;

  /** @param bufferedImage of type BufferedImage.TYPE_BYTE_GRAY */
  public BufferedImageRegion(BufferedImage bufferedImage, Tensor matrix, boolean outside) {
    this.bufferedImage = bufferedImage;
    this.matrix = matrix;
    affinePoint2D = new AffinePoint2D(Inverse.of(matrix));
    width = bufferedImage.getWidth();
    height = bufferedImage.getHeight();
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    data = dataBufferByte.getData();
    this.outside = outside;
  }

  @Override // from Region
  public boolean isMember(Tensor vector) {
    double px = vector.Get(0).number().doubleValue();
    double py = vector.Get(1).number().doubleValue();
    int x = (int) affinePoint2D.toX(px, py);
    if (0 <= x && x < width) {
      int y = (int) affinePoint2D.toY(px, py);
      if (0 <= y && y < height)
        return data[y * width + x] != 0;
    }
    return outside;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(matrix);
    graphics.drawImage(bufferedImage, //
        AffineTransforms.toAffineTransform(geometricLayer.getMatrix()), null);
    geometricLayer.popMatrix();
  }
}
