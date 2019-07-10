// code by gjoel
package ch.ethz.idsc.owl.rrts.core;

import ch.ethz.idsc.owl.data.tree.TreePlanner;

public interface TransitionPlanner extends TreePlanner<RrtsNode> {
  void checkConsistency();
}
