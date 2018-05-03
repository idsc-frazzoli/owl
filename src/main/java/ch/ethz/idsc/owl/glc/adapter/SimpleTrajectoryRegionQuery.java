// code by bapaden and jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Collection;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StandardTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.StateTimeCollector;
import ch.ethz.idsc.owl.math.state.TimeDependentRegion;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.Tensor;

/** default wrapper for obstacle and goal queries
 * implementation is abundantly used throughout the repository */
public class SimpleTrajectoryRegionQuery extends StandardTrajectoryRegionQuery implements StateTimeCollector {
  /** @param region that is queried with tensor = StateTime::state
   * @return */
  public static TrajectoryRegionQuery timeInvariant(Region<Tensor> region) {
    return new SimpleTrajectoryRegionQuery(new TimeInvariantRegion(region));
  }

  /** @param region that is queried with tensor = StateTime::joined
   * @return */
  public static TrajectoryRegionQuery timeDependent(Region<Tensor> region) {
    return new SimpleTrajectoryRegionQuery(new TimeDependentRegion(region));
  }

  /** @param region that is queried with StateTime */
  public SimpleTrajectoryRegionQuery(Region<StateTime> region) {
    // FIXME SparseStateTimeRegionMembers is not a good default option
    super(region, new SparseStateTimeRegionMembers());
  }

  /** Region members, which were found in Region
   * for GUI as only 1 State is allowed in 1 Raster (for sparsity)
   * 
   * @return Collection<StateTime> the members of the sparse raster */
  @Override // from StateTimeCollector
  public Collection<StateTime> getMembers() {
    return ((StateTimeCollector) getStateTimeRegionCallback()).getMembers();
  }
}
