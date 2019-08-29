// code by jph
package ch.ethz.idsc.owl.rrts.core;

import java.util.function.BiFunction;

import ch.ethz.idsc.owl.data.tree.SetNode;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Implementation based on
 * Sertac Karaman and Emilio Frazzoli, 2011:
 * Sampling-based algorithms for optimal motion planning
 * Algorithm 6, p.855 */
/* package */ final class RrtsNodeImpl extends SetNode<RrtsNode> implements RrtsNode {
  private final Tensor state;
  private Scalar costFromRoot;

  /* package */ RrtsNodeImpl(Tensor state, Scalar costFromRoot) {
    this.state = state;
    this.costFromRoot = costFromRoot;
  }

  @Override
  public RrtsNode connectTo(Tensor state, Scalar costFromRoot) {
    RrtsNode leaf = new RrtsNodeImpl(state, costFromRoot);
    this.insertEdgeTo(leaf);
    return leaf;
  }

  @Override
  public void rewireTo(RrtsNode child, BiFunction<RrtsNode, RrtsNode, Scalar> parentChildCost, final int influence) {
    // topology
    child.parent().removeEdgeTo(child); // "replace parent of given child ..."
    this.insertEdgeTo(child); // "... with this as new parent"
    // ---
    // costs
    // this.costFromRoot doesn't change
    // child.costFromRoot changes
    ((RrtsNodeImpl) child).costFromRoot = this.costFromRoot.add(parentChildCost.apply(this, child));
    // update subtree of child
    _propagate((RrtsNodeImpl) child, parentChildCost);
    // _propagate(child, childCostFromRoot);
    // ((RrtsNodeImpl) child).costFromRoot = childCostFromRoot;
  }

  // private static void _propagate(RrtsNode child, BiFunction<RrtsNode, RrtsNode, Scalar> parentChildCost, final int influence) {
  // if (0 < influence) {
  // ((RrtsNodeImpl) child).costFromRoot = null; // FIXME childCostFromRoot;
  // for (RrtsNode grandChild : child.children())
  // _propagate(grandChild, parentChildCost, influence - 1);
  // } else {
  // Scalar costFromParent = parentChildCost.apply(null, null);
  // _propagate(child, costFromParent);
  // }
  // }
  //
  // private static void _propagate(RrtsNode node, Scalar nodeCostFromRoot) {
  // for (RrtsNode _child : node.children()) {
  // RrtsNodeImpl child = (RrtsNodeImpl) _child;
  // final Scalar costFromParent = child.costFromRoot().subtract(node.costFromRoot());
  // Scalar newCostFromRoot = nodeCostFromRoot.add(costFromParent);
  // _propagate(child, newCostFromRoot);
  // child.costFromRoot = newCostFromRoot;
  // }
  // }
  private static void _propagate(RrtsNodeImpl node, BiFunction<RrtsNode, RrtsNode, Scalar> parentChildCost) {
    for (RrtsNode _child : node.children()) {
      RrtsNodeImpl child = (RrtsNodeImpl) _child;
      child.costFromRoot = node.costFromRoot.add(parentChildCost.apply(node, child));
      // final Scalar costFromParent = child.costFromRoot().subtract(node.costFromRoot());
      // Scalar newCostFromRoot = nodeCostFromRoot.add(costFromParent);
      _propagate(child, parentChildCost);
      // child.costFromRoot = newCostFromRoot;
    }
  }

  @Override
  public Tensor state() {
    return state;
  }

  @Override
  public Scalar costFromRoot() {
    return costFromRoot;
  }
}
