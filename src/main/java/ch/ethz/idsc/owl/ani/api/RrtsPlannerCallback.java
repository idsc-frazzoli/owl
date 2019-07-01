// code by jph, gjoel
package ch.ethz.idsc.owl.ani.api;

import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcTrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.RrtsPlannerServer;

/** TODO combine with {@link GlcPlannerCallback}
 * might require {@link RrtsPlannerServer} to be turned into {@link GlcTrajectoryPlanner} */
@FunctionalInterface
public interface RrtsPlannerCallback {
  void expandResult(List<TrajectorySample> head, RrtsPlannerServer rrtsPlannerServer);
}
