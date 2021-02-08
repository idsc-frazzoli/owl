// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.io.Serializable;
import java.util.Collection;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.Regions;
import ch.ethz.idsc.sophus.math.MinMax;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.nd.EuclideanNdCenter;
import ch.ethz.idsc.tensor.opt.nd.NdCenterInterface;
import ch.ethz.idsc.tensor.opt.nd.NdMap;
import ch.ethz.idsc.tensor.opt.nd.NdMatch;
import ch.ethz.idsc.tensor.opt.nd.NdTreeMap;
import ch.ethz.idsc.tensor.sca.Sign;

public class RnPointcloudRegion implements Region<Tensor>, Serializable {
  /** Example:
   * The points of a point cloud in the 2-dimensional plane are encoded as a N x 2 matrix.
   * 
   * @param points, matrix with dimensions N x D
   * @param radius non-negative
   * @return */
  public static Region<Tensor> of(Tensor points, Scalar radius) {
    Sign.requirePositiveOrZero(radius);
    return Tensors.isEmpty(points) //
        ? Regions.emptyRegion()
        : new RnPointcloudRegion(points, radius);
  }

  /***************************************************/
  private final Tensor points;
  private final Scalar radius;
  private final NdMap<Void> ndMap;

  /** @param points non-empty
   * @param radius */
  private RnPointcloudRegion(Tensor points, Scalar radius) {
    this.points = points.unmodifiable();
    this.radius = radius;
    MinMax minMax = MinMax.of(points);
    ndMap = new NdTreeMap<>(minMax.min(), minMax.max(), 5, 20); // magic const
    for (Tensor point : points)
      ndMap.add(point, null);
  }

  @Override // from Region
  public boolean isMember(Tensor tensor) {
    NdCenterInterface distanceInterface = EuclideanNdCenter.of(tensor);
    Collection<NdMatch<Void>> ndCluster = ndMap.cluster(distanceInterface, 1);
    Scalar distance = ndCluster.iterator().next().distance();
    return Scalars.lessEquals(distance, radius);
  }

  public Tensor points() {
    return points;
  }

  public Scalar radius() {
    return radius;
  }
}
