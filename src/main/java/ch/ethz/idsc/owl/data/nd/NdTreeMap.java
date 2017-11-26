// code by Eric Simonton
// adapted by jph and clruch
package ch.ethz.idsc.owl.data.nd;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;

public class NdTreeMap<V> implements NdMap<V> {
  private static final Scalar HALF = RationalScalar.of(1, 2);
  // ---
  private final int maxDensity;
  private final int maxDepth;
  private int size;
  private final Tensor global_lBounds;
  private final Tensor global_uBounds;
  // ---
  // reused during adding as well as searching:
  private Node root; // non final because of clear()
  private Tensor lBounds;
  private Tensor uBounds;

  /** lbounds and ubounds are vectors of identical length
   * for instance if the points to be added are in the unit square then
   * <pre>
   * lbounds = {0, 0}
   * ubounds = {1, 1}
   * </pre>
   * 
   * @param lbounds smallest coordinates of points to be added
   * @param ubounds greatest coordinates of points to be added
   * @param maxDensity is the maximum queue size of leaf nodes, except
   * for leaf nodes with maxDepth, which have unlimited queue size.
   * @param maxDepth 16 is reasonable for most applications */
  public NdTreeMap(Tensor lbounds, Tensor ubounds, int maxDensity, int maxDepth) {
    VectorQ.elseThrow(lbounds);
    VectorQ.elseThrow(ubounds);
    if (lbounds.length() != ubounds.length())
      throw TensorRuntimeException.of(lbounds, ubounds);
    if (!IntStream.range(0, lbounds.length()).allMatch(index -> Scalars.lessEquals(lbounds.Get(index), ubounds.Get(index))))
      throw TensorRuntimeException.of(lbounds, ubounds);
    global_lBounds = lbounds;
    global_uBounds = ubounds;
    this.maxDensity = maxDensity;
    this.maxDepth = maxDepth;
    clear();
  }

  /** @param location vector with same length as lbounds and ubounds
   * @param value */
  @Override
  public void add(Tensor location, V value) {
    if (!VectorQ.ofLength(location, global_lBounds.length()))
      throw TensorRuntimeException.of(location);
    add(new NdPair<V>(location, value));
  }

  private void add(NdPair<V> ndEntry) {
    resetBounds();
    root.add(ndEntry);
    ++size;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public NdCluster<V> buildCluster(NdCenterInterface ndCenter, int limit) {
    resetBounds();
    NdCluster<V> cluster = new NdCluster<V>(ndCenter, limit);
    root.addToCluster(cluster);
    return cluster;
  }

  @Override
  public void clear() {
    root = null;
    size = 0;
    root = new Node(maxDepth);
  }

  /** function returns the queue size of leaf nodes in the tree.
   * use the function to determine if the tree is well balanced.
   * 
   * <p>Example use:
   * Tally.sorted(Flatten.of(ndTreeMap.binSize()))
   * 
   * @return */
  public Tensor binSize() {
    return binSize(root);
  }

  private Tensor binSize(Node node) {
    if (Objects.isNull(node.queue))
      return Tensors.of( //
          Objects.isNull(node.lChild) ? RealScalar.ZERO : binSize(node.lChild), //
          Objects.isNull(node.rChild) ? RealScalar.ZERO : binSize(node.rChild) //
      );
    return RealScalar.of(node.queue.size());
  }

  private void resetBounds() {
    lBounds = global_lBounds.copy();
    uBounds = global_uBounds.copy();
  }

  private class Node implements Serializable {
    private final int depth;
    private Node lChild;
    private Node rChild;
    /** queue is set to null when node transform from leaf node to interior node */
    private Queue<NdPair<V>> queue = new ArrayDeque<>();

    private Node(int depth) {
      this.depth = depth;
    }

    private boolean internal() {
      return Objects.isNull(queue);
    }

    private int dimension() {
      return depth % lBounds.length();
    }

    private Scalar median(int index) {
      return lBounds.Get(index).add(uBounds.Get(index)).multiply(HALF);
    }

    private void add(final NdPair<V> ndEntry) {
      if (internal()) {
        Tensor location = ndEntry.location;
        int dimension = dimension();
        Scalar median = median(dimension);
        if (Scalars.lessThan(location.Get(dimension), median)) {
          uBounds.set(median, dimension);
          if (Objects.isNull(lChild))
            lChild = new Node(depth - 1);
          lChild.add(ndEntry);
          return;
        }
        lBounds.set(median, dimension);
        if (Objects.isNull(rChild))
          rChild = new Node(depth - 1);
        rChild.add(ndEntry);
      } else //
      if (queue.size() < maxDensity)
        queue.add(ndEntry);
      else //
      if (depth == 1)
        queue.add(ndEntry);
      // the original code removed a node from the queue: return queue.poll();
      // in our opinion this behavior is undesired.
      // at the lowest depth we grow the queue indefinitely, instead.
      else {
        int dimension = dimension();
        Scalar median = median(dimension);
        for (NdPair<V> entry : queue)
          if (Scalars.lessThan(entry.location.Get(dimension), median)) {
            if (Objects.isNull(lChild))
              lChild = new Node(depth - 1);
            lChild.queue.add(entry);
          } else {
            if (Objects.isNull(rChild))
              rChild = new Node(depth - 1);
            rChild.queue.add(entry);
          }
        queue = null;
        add(ndEntry);
      }
    }

    private void addToCluster(NdCluster<V> cluster) {
      if (internal()) {
        final int dimension = dimension();
        Scalar median = median(dimension);
        boolean lFirst = Scalars.lessThan(cluster.center.Get(dimension), median);
        addChildToCluster(cluster, median, lFirst);
        addChildToCluster(cluster, median, !lFirst);
      } else
        queue.forEach(cluster::consider);
    }

    private void addChildToCluster(NdCluster<V> cluster, Scalar median, boolean left) {
      final int dimension = dimension();
      if (left) {
        if (Objects.isNull(lChild))
          return;
        // ---
        Scalar copy = uBounds.Get(dimension);
        uBounds.set(median, dimension);
        if (cluster.isViable(lBounds, uBounds))
          lChild.addToCluster(cluster);
        uBounds.set(copy, dimension);
      } else {
        if (Objects.isNull(rChild))
          return;
        // ---
        Scalar copy = lBounds.Get(dimension);
        lBounds.set(median, dimension);
        if (cluster.isViable(lBounds, uBounds))
          rChild.addToCluster(cluster);
        lBounds.set(copy, dimension);
      }
    }
  }

  /***************************************************/
  // functions for testing
  /* package */ void print() {
    print(root);
  }

  private void print(Node node) {
    String v = IntStream.range(0, root.depth - node.depth) //
        .mapToObj(i -> " ") //
        .collect(Collectors.joining());
    if (Objects.isNull(node.queue)) {
      System.out.println(v + "<empty>");
      if (Objects.nonNull(node.lChild))
        print(node.lChild);
      if (Objects.nonNull(node.rChild))
        print(node.rChild);
    } else {
      GlobalAssert.that(Objects.isNull(node.lChild));
      GlobalAssert.that(Objects.isNull(node.rChild));
      System.out.println(v + "" + node.queue.size());
      for (NdPair<V> entry : node.queue)
        System.out.println(v + "" + entry.location + " " + entry.value());
    }
  }
}
