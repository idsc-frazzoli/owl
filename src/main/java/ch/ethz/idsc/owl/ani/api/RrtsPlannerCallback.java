// code by jph
package ch.ethz.idsc.owl.ani.api;

import java.util.List;

import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.core.RrtsPlanner;

/**
 * 
 */
// API not finalized
public interface RrtsPlannerCallback {
  void expandResult(List<TrajectorySample> head, RrtsPlanner rrtsPlanner, List<TrajectorySample> tail);
}
