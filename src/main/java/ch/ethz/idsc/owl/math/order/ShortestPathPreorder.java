// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.owl.data.tree.Node;

/** Preorder for shortest path
 * <p>Shortest path in this sense means the path of shortest length.
 * <p>Length is defined as the number of edges from initial vertex to end vertex. */
public class ShortestPathPreorder implements BinaryRelation<Node> {
  public int totalLengthNode(Node x) {
    if (x.isRoot()) {
      return 0;
    } else {
      return 1 + totalLengthNode(x.parent());
    }
  }

  @Override // from BinaryRelation
  public boolean test(Node x, Node y) {
    return (totalLengthNode(x) < totalLengthNode(y));
  }
}
