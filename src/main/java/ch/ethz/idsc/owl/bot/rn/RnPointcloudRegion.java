// code by jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.owl.data.nd.NdCluster;
import ch.ethz.idsc.owl.data.nd.NdMap;
import ch.ethz.idsc.owl.data.nd.NdTreeMap;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.Regions;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Entrywise;

public class RnPointcloudRegion implements Region<Tensor> {
  /** Example:
   * The points of a point cloud in the 2-dimensional plane are encoded as a N x 2 matrix.
   * 
   * @param points, matrix with dimensions N x D
   * @param radius
   * @return */
  public static Region<Tensor> of(Tensor points, Scalar radius) {
    return Tensors.isEmpty(points) ? Regions.emptyRegion() : new RnPointcloudRegion(points, radius);
  }

  // ---
  private final Tensor points;
  private final Scalar radius;
  private final NdMap<Void> ndMap;

  /** @param points non-empty
   * @param radius */
  private RnPointcloudRegion(Tensor points, Scalar radius) {
    this.points = points.unmodifiable();
    this.radius = radius;
    ndMap = new NdTreeMap<>( //
        points.stream().reduce(Entrywise.min()).get(), //
        points.stream().reduce(Entrywise.max()).get(), //
        5, 20); // magic const
    for (Tensor point : points)
      ndMap.add(point, null);
  }

  @Override
  public boolean isMember(Tensor tensor) {
    NdCenterInterface distanceInterface = NdCenterInterface.euclidean(tensor);
    NdCluster<Void> ndCluster = ndMap.buildCluster(distanceInterface, 1);
    // System.out.println(ndCluster.considered() + " / " + ndMap.size());
    Scalar distance = ndCluster.collection().iterator().next().distance();
    return Scalars.lessEquals(distance, radius);
  }

  public Tensor points() {
    return points;
  }

  public Scalar radius() {
    return radius;
  }
}
