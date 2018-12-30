// code by ynager, found on github
package ch.ethz.idsc.owl.data.img;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;

/** every pixel is converted to a rectangle and joined with an area.
 * the technique is not very fast. */
public enum ImageArea {
  ;
  /** for an image with dimensions 640x640 the function takes ~6[s]
   * 
   * @param bufferedImage
   * @return area with all non-black pixels as member */
  public static Area fromImage(BufferedImage bufferedImage) {
    Area area = new Area();
    for (int y = 0; y < bufferedImage.getHeight(); ++y)
      for (int x = 0; x < bufferedImage.getWidth(); ++x)
        if ((bufferedImage.getRGB(x, y) & 0xffffff) != 0) // ff000000 or ff000000
          area.add(new Area(new Rectangle(x, y, 1, 1)));
    return area;
  }

  /** @param tensor
   * @return */
  public static Area fromTensor(Tensor tensor) {
    Area area = new Area();
    int dimY = tensor.length();
    int dimX = Unprotect.dimension1(tensor);
    for (int x = 0; x < dimX; ++x)
      for (int y = 0; y < dimY; ++y)
        if (Scalars.nonZero(tensor.Get(y, x)))
          area.add(new Area(new Rectangle(x, y, 1, 1)));
    return area;
  }
}
