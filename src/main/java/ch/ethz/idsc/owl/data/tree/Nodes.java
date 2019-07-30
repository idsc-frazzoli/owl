// code by jph
package ch.ethz.idsc.owl.data.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/** utility functions */
public enum Nodes {
  ;
  // ---
  /** @param node
   * @return root that is the result of visiting the parents from given start node */
  @SuppressWarnings("unchecked")
  public static <T extends Node> T rootFrom(T node) {
    Node root = node;
    while (Objects.nonNull(root.parent()))
      root = root.parent();
    return (T) root;
  }

  /** @param node
   * @return */
  @SuppressWarnings("unchecked")
  public static <T extends Node> List<T> listToRoot(T node) {
    Objects.requireNonNull(node);
    List<T> list = new ArrayList<>();
    while (Objects.nonNull(node)) {
      list.add(node);
      node = (T) node.parent();
    }
    return list;
  }

  /** @param node
   * @return */
  public static <T extends Node> List<T> listFromRoot(T node) {
    List<T> list = listToRoot(node);
    Collections.reverse(list);
    return list;
  }

  /** @param node
   * @param n non-negative
   * @return n'th degree parent of node; 0 for given node, 1 for parent of given node, etc. */
  @SuppressWarnings("unchecked")
  public static <T extends Node> T getParent(T node, int n) {
    if (0 <= n) {
      Node parent = node;
      for (int i = 0; Objects.nonNull(parent.parent()) && i < n; ++i)
        parent = parent.parent();
      return (T) parent;
    }
    throw new RuntimeException("" + n);
  }

  /** @param node1
   * @param node2
   * @return */
  public static <T extends Node> boolean areConnected(T node1, T node2) {
    return Nodes.listToRoot(node1).contains(node2) //
        || Nodes.listToRoot(node2).contains(node1);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Node> void ofSubtree(T node, Collection<T> collection) {
    collection.add(node);
    node.children().stream().forEach(child -> ofSubtree((T) child, collection));
  }

  /** applications may sort the collection, for instance {@link OptimalAnyTrajectoryPlanner}
   * 
   * @param node
   * @return */
  public static <T extends Node> Collection<T> ofSubtree(T node) {
    Collection<T> collection = new LinkedList<>();
    ofSubtree(node, collection);
    return collection;
  }

  public static <T extends Node> T disjoinAt(T node) {
    @SuppressWarnings("unchecked")
    T parent = (T) node.parent();
    if (Objects.nonNull(parent))
      parent.removeEdgeTo(node);
    return parent;
  }

  /** @param node
   * @throws Exception if given child has no parent
   * @see Nodes#disjoinAt(Node) */
  public static void disjoinChild(Node node) {
    if (node.parent() == null)
      throw new RuntimeException("Node is root!");
    node.parent().removeEdgeTo(node);
  }
}
