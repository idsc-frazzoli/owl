// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.state.StateTime;

public enum GlcNodes {
  ;
  /** coarse path of {@link StateTime}s of nodes from root to given node
   * 
   * @return path to goal if found, or path to current Node in queue
   * @throws Exception if node is null */
  public static List<StateTime> getPathFromRootTo(GlcNode node) {
    return Nodes.listFromRoot(node).stream() //
        .map(GlcNode::stateTime) //
        .collect(Collectors.toList());
  }
}
