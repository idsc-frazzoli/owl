// code from stackoverflow  
// adapted by jph
package ch.ethz.idsc.owl.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DisjointSets {
  private final List<Node> list = new ArrayList<>();

  /** @return index of added set */
  public int add() {
    int index = list.size();
    list.add(new Node(index));
    return index;
  }

  /** @param index1
   * @param index2 */
  public void union(int index1, int index2) {
    int key_x = key(index1);
    int key_y = key(index2);
    if (key_x != key_y) {
      Node node_x = list.get(key_x);
      Node node_y = list.get(key_y);
      if (node_x.rank < node_y.rank)
        node_x.setParent(key_y);
      else //
      if (node_x.rank > node_y.rank)
        node_y.setParent(key_x);
      else {
        node_y.setParent(key_x);
        ++node_x.rank;
      }
    }
  }

  /** @param index
   * @return representative of index */
  public int key(int index) {
    Node node = list.get(index);
    if (node.parent != index)
      node.setParent(key(node.parent)); // path collapse
    return node.parent;
  }

  public <T> Map<Integer, T> createMap(Supplier<T> supplier) {
    // list.stream().map(n -> n.parent).map(this::key).distinct();
    return IntStream.range(0, list.size()) //
        .map(this::key) //
        .boxed() //
        .distinct() //
        .collect(Collectors.toMap(Function.identity(), key -> supplier.get()));
  }

  /***************************************************/
  private static class Node {
    private int parent;
    private Integer rank = 0;

    public Node(int index) {
      parent = index;
    }

    public void setParent(int index) {
      parent = index;
      rank = null;
    }
  }

  /***************************************************/
  // functions for testing
  /* package */ int depth(int index) {
    int depth = 0;
    while (list.get(index).parent != index) {
      index = list.get(index).parent;
      ++depth;
    }
    return depth;
  }

  /* package */ Collection<Integer> parents() {
    return list.stream().map(n -> n.parent).collect(Collectors.toSet());
  }

  /* package */ Collection<Integer> representatives() {
    return IntStream.range(0, list.size()) //
        .map(this::key) //
        .boxed().collect(Collectors.toSet());
  }
}
