// code by jl
package ch.ethz.idsc.owl.data.tree;

import java.util.Collection;
import java.util.Iterator;

public enum NodesAssert {
  ;
  /** @param best
   * @param size */
  public static <T extends Node> void check(T best, int size) {
    final T root = Nodes.rootFrom(best);
    if (size != Nodes.ofSubtree(root).size()) {
      System.out.println("****NODE CHECK****");
      System.out.println("Nodes in DomainMap: " + size);
      System.out.println("Nodes in SubTree from Node: " + Nodes.ofSubtree(best).size());
      throw new RuntimeException();
    }
  }

  /** @param collection */
  public static <T extends Node> void connectivityCheck(Collection<T> collection) {
    for (Iterator<T> iterator = collection.iterator(); iterator.hasNext();) {
      Node node = iterator.next();
      if (!node.isRoot() && //
          !node.parent().children().contains(node))
        throw new RuntimeException();
    }
  }

  public static <T extends Node> void containsOneRoot(Collection<T> collection) {
    long count = collection.stream().filter(Node::isRoot).limit(2).count();
    if (count != 1)
      throw new RuntimeException("root count=" + count);
  }

  public static <T extends Node> void allLeaf(Collection<T> collection) {
    boolean allLeaf = collection.stream().allMatch(Node::isLeaf);
    if (!allLeaf)
      throw new RuntimeException("Not all elements in global queue are leafs!");
  }
}
