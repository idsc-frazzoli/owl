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
  private static final long serialVersionUID = -5774540873039812999L;
  // ---
  private final Tensor state;
  private Scalar costFromRoot;

  /* package */ RrtsNodeImpl(Tensor state, Scalar costFromRoot) {
    this.state = state;
    this.costFromRoot = costFromRoot;
  }

  @Override // from StateCostNode
  public Tensor state() {
    return state;
  }

  @Override // from StateCostNode
  public Scalar costFromRoot() {
    return costFromRoot;
  }

  @Override // from RrtsNode
  public RrtsNode connectTo(Tensor state, Scalar costFromRoot) {
    RrtsNode leaf = new RrtsNodeImpl(state, costFromRoot);
    this.insertEdgeTo(leaf);
    return leaf;
  }

  /***************************************************/
  @Override // from RrtsNode
  public void rewireTo(RrtsNode child, BiFunction<RrtsNode, RrtsNode, Scalar> edgeCost, int influence) {
    // topology
    child.parent().removeEdgeTo(child); // "replace parent of given child ..."
    this.insertEdgeTo(child); // "... with this as new parent"
    // ---
    // costs
    // this.costFromRoot doesn't change
    Scalar child_costFromRoot = this.costFromRoot.add(edgeCost.apply(this, child));
    // update subtree of child
    _propagate((RrtsNodeImpl) child, child_costFromRoot, edgeCost, influence);
    // child.costFromRoot changes
    ((RrtsNodeImpl) child).costFromRoot = child_costFromRoot;
  }

  private static void _propagate(RrtsNodeImpl node, Scalar node_costFromRoot, BiFunction<RrtsNode, RrtsNode, Scalar> edgeCost, int influence) {
    if (0 < influence)
      for (RrtsNode _child : node.children()) {
        RrtsNodeImpl child = (RrtsNodeImpl) _child;
        Scalar child_costFromRoot = node_costFromRoot.add(edgeCost.apply(node, child));
        _propagate(child, child_costFromRoot, edgeCost, influence - 1);
        child.costFromRoot = child_costFromRoot;
      }
    else
      _propagate(node, node_costFromRoot);
  }

  private static void _propagate(RrtsNodeImpl node, final Scalar node_costFromRoot) {
    for (RrtsNode _child : node.children()) {
      RrtsNodeImpl child = (RrtsNodeImpl) _child;
      Scalar costFromParent = child.costFromRoot().subtract(node.costFromRoot());
      Scalar child_costFromRoot = node_costFromRoot.add(costFromParent);
      _propagate(child, child_costFromRoot);
      child.costFromRoot = child_costFromRoot;
    }
  }

  /***************************************************/
  /** simple brute force method that does not reuse old cost information
   * 
   * @param node
   * @param parentChildCost */
  // function is not used
  static void _propagate(RrtsNodeImpl node, Scalar node_costFromRoot, BiFunction<RrtsNode, RrtsNode, Scalar> parentChildCost) {
    for (RrtsNode _child : node.children()) {
      RrtsNodeImpl child = (RrtsNodeImpl) _child;
      Scalar child_costFromRoot = node_costFromRoot.add(parentChildCost.apply(node, child));
      _propagate(child, child_costFromRoot, parentChildCost);
      child.costFromRoot = child_costFromRoot;
    }
  }
}
