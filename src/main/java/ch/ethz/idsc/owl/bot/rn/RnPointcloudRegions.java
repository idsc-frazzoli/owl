// code by jph and jl
package ch.ethz.idsc.owl.bot.rn;

import java.util.List;

import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.sca.N;

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
   * @param imageRegion
   * @param radius
   * @return */
  public static Region<Tensor> from(ImageRegion imageRegion, Scalar radius) {
    Tensor points = from(imageRegion);
    System.out.println(Dimensions.of(points));
    return RnPointcloudRegion.of(points, radius);
  }

  public static Tensor from(ImageRegion imageRegion) {
    Tensor inverse = N.DOUBLE.of(imageRegion.scale().map(Scalar::reciprocal));
    Tensor tensor = imageRegion.image();
    List<Integer> dimensions = Dimensions.of(tensor);
    Tensor points = Tensors.empty();
    final int rows = dimensions.get(0);
    for (int row = 0; row < rows; ++row)
      for (int col = 0; col < dimensions.get(1); ++col) {
        Scalar occ = tensor.Get(row, col);
        if (Scalars.nonZero(occ))
          points.append(Tensors.vector(col, rows - row).pmul(inverse));
      }
    return points;
  }
}
