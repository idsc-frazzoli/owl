// code by gjoel
package ch.ethz.idsc.owl.rrts.core;

import java.util.Collection;

import ch.ethz.idsc.owl.data.tree.ExpandInterface;

public interface RrtsPlanner extends ExpandInterface<RrtsNode> {
  /** @return unmodifiable view on queue for display and tests */
  Collection<RrtsNode> getQueue();

  /** @return obstacle query */
  TransitionRegionQuery getObstacleQuery();
}
