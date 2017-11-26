// code by jl
package ch.ethz.idsc.owl.bot.rn;

import java.util.Arrays;

import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionIntersection;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum EuclideanDistanceDiscoverRegion {
  ;
  /** @param region
   * @param origin
   * @param distance
   * @return */
  public static Region<Tensor> of(Region<Tensor> region, Tensor origin, Scalar distance) {
    if (origin.length() == 3)
      // TODO Enhance to any statespace in the assumption that the first 2 are x and y
      return RegionIntersection.wrap(Arrays.asList( //
          new EllipsoidRegion(origin, Tensors.of(distance, distance, DoubleScalar.POSITIVE_INFINITY)), region));
    return RegionIntersection.wrap(Arrays.asList( //
        new SphericalRegion(origin, distance), region));
  }
}
