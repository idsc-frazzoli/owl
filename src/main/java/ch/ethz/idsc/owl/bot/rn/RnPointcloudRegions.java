// code by jph and jl
package ch.ethz.idsc.owl.bot.rn;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.math.region.BufferedImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum RnPointcloudRegions {
  ;
  /** @param num number of points
   * @param width of area, in which they are created
   * @param offset of area, in which they are created
   * @param radius of each obstacle
   * @return region with random points as obstacles */
  public static Region<Tensor> createRandomRegion(int num, Tensor offset, Tensor width, Scalar radius) {
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(offset, offset.add(width));
    return RnPointcloudRegion.of(RandomSample.of(randomSampleInterface, num), radius);
  }

  /** extrusion of non-zero pixels in given image by fixed radius
   * 
   * @param bufferedImageRegion
   * @param radius non-negative
   * @return */
  public static Region<Tensor> from(Region<Tensor> bufferedImageRegion, Scalar radius) {
    return RnPointcloudRegion.of(points((BufferedImageRegion) bufferedImageRegion), radius);
  }

  /* package */ static Tensor points(BufferedImageRegion bufferedImageRegion) {
    Tensor points = Tensors.empty();
    BufferedImage bufferedImage = bufferedImageRegion.bufferedImage();
    Tensor pixel2model = bufferedImageRegion.pixel2model();
    int cols = bufferedImage.getWidth();
    int rows = bufferedImage.getHeight();
    for (int row = 0; row < rows; ++row)
      for (int col = 0; col < cols; ++col) {
        Tensor vector = pixel2model.dot(Tensors.vector(col, row, 1));
        if (bufferedImageRegion.isMember(vector))
          points.append(Extract2D.FUNCTION.apply(vector));
      }
    return points;
  }
}
