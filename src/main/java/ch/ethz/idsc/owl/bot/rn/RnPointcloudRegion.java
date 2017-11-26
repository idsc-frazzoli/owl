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
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;

public class RnPointcloudRegion implements Region<Tensor> {
  /** @param points
   * @param radius
   * @return */
  public static Region<Tensor> of(Tensor points, Scalar radius) {
    return Tensors.isEmpty(points) ? Regions.emptyRegion() : new RnPointcloudRegion(points, radius);
  }

  // ---
  private final Tensor points;
  private final Scalar radius;
  private final NdMap<Void> ndMap;

  private RnPointcloudRegion(Tensor points, Scalar radius) {
    this.points = points.unmodifiable();
    this.radius = radius;
    Tensor pt = Transpose.of(points);
    Tensor lbounds = Tensors.vector(i -> pt.get(i).stream().reduce(Min::of).get(), pt.length());
    Tensor ubounds = Tensors.vector(i -> pt.get(i).stream().reduce(Max::of).get(), pt.length());
    // System.out.println("---");
    // System.out.println(lbounds);
    // System.out.println(ubounds);
    ndMap = new NdTreeMap<>(lbounds, ubounds, 5, 20);
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
