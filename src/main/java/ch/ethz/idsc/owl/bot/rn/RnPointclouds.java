// code by jph and jl
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.sample.BoxRandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSample;
import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum RnPointclouds {
  ;
  /** @param num number of points
   * @param width of area, in which they are created
   * @param offset of area, in which they are created
   * @param radius of each obstacle
   * @return region with random points as obstacles */
  public static Region<Tensor> createRandomRegion(int num, Tensor offset, Tensor width, Scalar radius) {
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(offset, offset.add(width));
    return RnPointcloudRegion.of(RandomSample.of(randomSampleInterface, num), radius);
  }
}
