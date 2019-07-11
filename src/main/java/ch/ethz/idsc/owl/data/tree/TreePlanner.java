// code by ynager
package ch.ethz.idsc.owl.data.tree;

import java.util.Collection;
import java.util.Optional;

import ch.ethz.idsc.owl.math.state.StateTime;

/** grows a tree of nodes */
public interface TreePlanner<T extends StateCostNode> extends ExpandInterface<T> {
  /** @param stateTime */
  void insertRoot(StateTime stateTime);

  /** @return best node known to be in goal, or top node in queue, or null,
   * in this order depending on existence */
  Optional<T> getBestOrElsePeek();

  /** @return unmodifiable view on queue for display and tests */
  Collection<T> getQueue();
}
