// code by bapaden, jph and jl
package ch.ethz.idsc.owl.glc.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.ethz.idsc.owl.data.tree.AbstractNode;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** glc specific node
 * 
 * immutable except for children, parent, and depth which are only modified in
 * {@link GlcNodeImpl#addChild(GlcNodeImpl)} */
/* package */ class GlcNodeImpl extends AbstractNode<GlcNode> implements GlcNode {
  private final Map<Tensor, GlcNode> children = new HashMap<>();
  /** flow is null for root node
   * not final, as changed when central node are made root */
  private final Tensor flow;
  private final StateTime stateTime;
  /** accumulation of costs from root to this node.
   * costFromRoot only depends on the past {@link StateTime}s and {@link Flow}s.
   * costFromRoot is independent of a "goal". */
  private final Scalar costFromRoot;
  /** merit == costFromRoot + "Heuristic.minCostToGoal" */
  private final Scalar merit;

  /** @param flow that got us to this Node from the parent, or null when this Node is the root
   * @param stateTime
   * @param costFromRoot
   * @param minCostToGoal non-negative, provided by {@link HeuristicFunction},
   * or {@link RealScalar#ZERO} */
  GlcNodeImpl(Tensor flow, StateTime stateTime, Scalar costFromRoot, Scalar minCostToGoal) {
    this.flow = flow;
    this.stateTime = stateTime;
    this.costFromRoot = costFromRoot;
    merit = costFromRoot.add(minCostToGoal);
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

  @Override // from AbstractNode
  protected boolean protected_insertChild(GlcNode child) {
    boolean inserted = !children.containsKey(child.flow());
    children.put(child.flow(), child);
    return inserted;
  }

  @Override // from AbstractNode
  protected boolean protected_removeChild(GlcNode child) {
    boolean removed = children.containsKey(child.flow());
    children.remove(child.flow());
    return removed;
  }

  @Override // from GlcNode
  public Tensor flow() {
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
}
