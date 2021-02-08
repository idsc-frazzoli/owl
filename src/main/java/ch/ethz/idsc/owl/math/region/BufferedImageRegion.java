// code by jph
package ch.ethz.idsc.owl.math.region;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.Serializable;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;

/** region in R2 */
public class BufferedImageRegion implements Region<Tensor>, RenderInterface, Serializable {
  private transient final BufferedImage bufferedImage;
  private transient final AffineFrame affineFrame;
  private final Tensor pixel2model;
  private final int width;
  private final int height;
  private final byte[] data;
  private final boolean outside;

  /** @param bufferedImage of type BufferedImage.TYPE_BYTE_GRAY
   * @param pixel2model with dimension 3 x 3
   * @param outside membership */
  public BufferedImageRegion(BufferedImage bufferedImage, Tensor pixel2model, boolean outside) {
    if (bufferedImage.getType() != BufferedImage.TYPE_BYTE_GRAY)
      throw new IllegalArgumentException("" + bufferedImage.getType());
    this.bufferedImage = bufferedImage;
    this.pixel2model = pixel2model.copy();
    affineFrame = new AffineFrame(Inverse.of(pixel2model));
    width = bufferedImage.getWidth();
    height = bufferedImage.getHeight();
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    data = dataBufferByte.getData();
    this.outside = outside;
  }

  @Override // from Region
  public final boolean isMember(Tensor vector) {
    return isMember( //
        vector.Get(0).number().doubleValue(), //
        vector.Get(1).number().doubleValue());
  }

  public boolean isMember(double px, double py) {
    int x = (int) affineFrame.toX(px, py);
    if (0 <= x && x < width) {
      int y = (int) affineFrame.toY(px, py);
      if (0 <= y && y < height)
        return data[y * width + x] != 0;
    }
    return outside;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(pixel2model);
    graphics.drawImage(bufferedImage, //
        AffineTransforms.toAffineTransform(geometricLayer.getMatrix()), null);
    geometricLayer.popMatrix();
  }

  /** @return bufferedImage of type BufferedImage.TYPE_BYTE_GRAY */
  public BufferedImage bufferedImage() {
    return bufferedImage;
  }

  public Tensor pixel2model() {
    return pixel2model.unmodifiable();
  }
}
