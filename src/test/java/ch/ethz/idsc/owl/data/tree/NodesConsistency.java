// code by jl
package ch.ethz.idsc.owl.data.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.owl.data.GlobalAssert;

public enum NodesConsistency {
  ;
  public static <T extends Node> void check(T best, int size) {
    final T root = Nodes.rootFrom(best);
    if (size != Nodes.ofSubtree(root).size()) {
      System.out.println("****NODE CHECK****");
      System.out.println("Nodes in DomainMap: " + size);
      System.out.println("Nodes in SubTree from Node: " + Nodes.ofSubtree(best).size());
      throw new RuntimeException();
    }
  }

  public static <T extends Node> void connectivityCheck(Collection<T> treeCollection) {
    for (Iterator<T> iterator = treeCollection.iterator(); iterator.hasNext();) {
      Node node = iterator.next();
      if (!node.isRoot())
        GlobalAssert.that(node.parent().children().contains(node));
    }
    if (treeCollection instanceof List<?>) {
      GlobalAssert.that(((List<T>) treeCollection).get(0).isRoot());
      for (int i = 1; i < treeCollection.size(); ++i) {
        T node = ((List<T>) treeCollection).get(i);
        T previous = ((List<T>) treeCollection).get(i - 1);
        GlobalAssert.that(node.parent() == previous);
      }
    }
  }
}
