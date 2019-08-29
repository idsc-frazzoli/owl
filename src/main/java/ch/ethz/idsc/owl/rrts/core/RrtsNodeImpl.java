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
  public void rewireTo( //
      RrtsNode child, Scalar costFromParent, //
      BiFunction<RrtsNode, RrtsNode, Scalar> parentChildCost, final int influence) {
    // topology
    child.parent().removeEdgeTo(child); // "replace parent of given child ..."
    this.insertEdgeTo(child); // "... with this as new parent"
    // ---
    // costs
    final Scalar childCostFromRoot = costFromRoot().add(costFromParent);
    /* // the condition of cost reduction is not strictly necessary
     * if (!Scalars.lessThan(nodeCostFromRoot, child.costFromRoot()))
     * throw TensorRuntimeException.of(nodeCostFromRoot, child.costFromRoot()); */
    // if (0 < influence) {
    // ((RrtsNodeImpl) child).costFromRoot = childCostFromRoot;
    // for (RrtsNode grandChild : child.children())
    _propagate(this, parentChildCost.apply(this, child), parentChildCost, influence);
    // } else {
    // _propagate(child, childCostFromRoot);
    // ((RrtsNodeImpl) child).costFromRoot = childCostFromRoot;
    // }
  }

  private static void _propagate(RrtsNode node, Scalar costFromParent, BiFunction<RrtsNode, RrtsNode, Scalar> parentChildCost, final int influence) {
    if (0 < influence) {
      ((RrtsNodeImpl) node).costFromRoot = null; // FIXME childCostFromRoot;
      for (RrtsNode child : node.children())
        _propagate(child, parentChildCost.apply(node, child), parentChildCost, influence - 1);
    } else
      _propagate(node, costFromParent);
  }

  private static void _propagate(RrtsNode node, Scalar nodeCostFromRoot) {
    for (RrtsNode _child : node.children()) {
      RrtsNodeImpl child = (RrtsNodeImpl) _child;
      final Scalar costFromParent = child.costFromRoot().subtract(node.costFromRoot());
      Scalar newCostFromRoot = nodeCostFromRoot.add(costFromParent);
      _propagate(child, newCostFromRoot);
      child.costFromRoot = newCostFromRoot;
    }
    // node.children().stream().parallel().forEach(child -> {
    // final Scalar costFromParent = child.costFromRoot().subtract(node.costFromRoot());
    // Scalar newCostFromRoot = nodeCostFromRoot.add(costFromParent);
    // _propagate(child, newCostFromRoot);
    // ((RrtsNodeImpl) child).costFromRoot = newCostFromRoot;
    // });
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
