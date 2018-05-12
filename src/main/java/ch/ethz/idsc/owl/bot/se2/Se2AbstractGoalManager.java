// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.io.Serializable;

import ch.ethz.idsc.owl.data.DontModify;
import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.adapter.GoalAdapter;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** suggested base class for se2 goal managers.
 * all implemented methods in this layer are final.
 * 
 * class defines circle region for (x,y) component and periodic intervals in angular component */
@DontModify
public abstract class Se2AbstractGoalManager implements Region<Tensor>, CostFunction, Serializable {
  private final SphericalRegion sphericalRegion;
  private final So2Region so2Region;
  protected final Tensor center; // TODO variable no good
  protected final Tensor radiusVector;

  /** @param center of region with coordinates (x, y, theta)
   * @param radiusVector with 3 entries the first 2 of which have to be identical */
  protected Se2AbstractGoalManager(Tensor center, Tensor radiusVector) {
    GlobalAssert.that(radiusVector.get(0).equals(radiusVector.get(1)));
    GlobalAssert.that(VectorQ.ofLength(center, 3));
    GlobalAssert.that(VectorQ.ofLength(radiusVector, 3));
    sphericalRegion = new SphericalRegion(center.extract(0, 2), radiusVector.Get(0));
    so2Region = new So2Region(center.Get(2), radiusVector.Get(2));
    this.center = center.unmodifiable();
    this.radiusVector = radiusVector.unmodifiable();
  }

  protected final Scalar radiusSpace() {
    return radiusVector.Get(0);
  }

  /** @param tensor == {px, py, angle}
   * @return signed distance of {px, py} from spherical region */
  protected final Scalar d_xy(Tensor tensor) {
    return sphericalRegion.signedDistance(tensor.extract(0, 2));
  }

  /** @param tensor == {px, py, angle}
   * @return signed distance of angle from so2region */
  protected final Scalar d_angle(Tensor tensor) {
    return so2Region.signedDistance(tensor.get(2));
  }

  @Override // from Region
  public final boolean isMember(Tensor tensor) {
    return sphericalRegion.isMember(tensor.extract(0, 2)) && so2Region.isMember(tensor.get(2));
  }

  public final GoalInterface getGoalInterface() {
    return new GoalAdapter(SimpleTrajectoryRegionQuery.timeInvariant(this), this);
  }
}
