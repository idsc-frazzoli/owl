// code by bapaden, jph and jl
package ch.ethz.idsc.owl.glc.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.data.tree.AbstractNode;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** glc specific node
 * 
 * immutable except for children, parent, and depth which are only modified in
 * {@link GlcNodeImpl#addChild(GlcNodeImpl)} */
/* package */ class GlcNodeImpl extends AbstractNode<GlcNode> implements GlcNode {
  private final Map<Flow, GlcNode> children = new HashMap<>();
  /** flow is null for root node
   * not final, as changed when central node are made root */
  private Flow flow;
  private final StateTime stateTime;
  /** accumulation of costs from root to this node.
   * costFromRoot only depends on the past {@link StateTime}s and {@link Flow}s.
   * costFromRoot is independent of a "goal". */
  private Scalar costFromRoot;
  /** merit == costFromRoot + "Heuristic.minCostToGoal" */
  private Scalar merit;
  /** depth == 0 for root node, otherwise depth > 0 */
  private int depth = 0;

  /** @param flow that got us to this Node from the parent, or null when this Node is the root
   * @param stateTime
   * @param costFromRoot
   * @param minCostToGoal */
  GlcNodeImpl(Flow flow, StateTime stateTime, Scalar costFromRoot, Scalar minCostToGoal) {
    this.flow = flow;
    this.stateTime = stateTime;
    this.costFromRoot = costFromRoot;
    setMinCostToGoal(minCostToGoal);
  }

  @Override // from Node
  public Collection<GlcNode> children() {
    return Collections.unmodifiableCollection(children.values());
  }

  @Override // from StateCostNode
  public Tensor state() {
    return stateTime.state();
  }

  @Override // from StateCostNode
  public Scalar costFromRoot() {
    return costFromRoot;
  }

  /* at the moment both functions
   * protected_insertChild, and protected_removeChild
   * are used to _temporarily_ alter the tree structure
   * when undoing the connectivity change the original
   * content is required, therefore content is not deleted.
   * instead use makeRoot() for instance. */
  @Override // from AbstractNode
  protected boolean protected_insertChild(GlcNode child) {
    boolean inserted = !children.containsKey(child.flow());
    children.put(child.flow(), child);
    ((GlcNodeImpl) child).depth = depth + 1;
    return inserted;
  }

  @Override // from AbstractNode
  protected final boolean protected_removeChild(GlcNode child) {
    boolean removed = children.containsKey(child.flow());
    children.remove(child.flow());
    return removed;
  }

  @Override // from GlcNode
  public Flow flow() {
    return flow;
  }

  @Override // from GlcNode
  public StateTime stateTime() {
    return stateTime;
  }

  /** @return cost from root plus min cost to goal */
  @Override // from GlcNode
  public Scalar merit() {
    return merit;
  }

  @Override // from GlcNode
  public int depth() {
    return depth;
  }

  @Override // from GlcNode
  public void setMinCostToGoal(Scalar minCostToGoal) {
    merit = costFromRoot.add(minCostToGoal);
  }

  @Override // from GlcNode
  public int depthDifferenceToRoot() {
    return Nodes.listToRoot(this).size() - 1;
  }

  @Override // from GlcNode
  public void makeRoot() {
    // System.err.println("HERE MAKE ROOT");
    if (isRoot()) {
      if (Objects.nonNull(flow))
        throw new RuntimeException();
    } else {
      parent().removeEdgeTo(this);
      flow = null; // TODO this should be handled in removeEdgeTo
      GlobalAssert.that(flow == null);
    }
  }
}
