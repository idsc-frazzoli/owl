// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

/** used in se2 animation demo to check if footprint of vehicle intersects with obstacle region */
public class Se2PointsVsRegion implements Region<Tensor> {
  private final Tensor points;
  private final Region<Tensor> region;

  public Se2PointsVsRegion(Tensor points, Region<Tensor> region) {
    this.points = points.copy().unmodifiable();
    this.region = region;
  }

  /** @param tensor of the form (x,y,theta)
   * @return true if any of the points subject to the given transformation are in region */
  @Override
  public boolean isMember(Tensor tensor) {
    Se2Bijection se2Bijection = new Se2Bijection(tensor);
    return points.stream().map(se2Bijection.forward()).anyMatch(region::isMember);
  }

  public Tensor points() {
    return points;
  }
}
