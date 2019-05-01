// code by Eric Simonton
// adapted by jph and clruch
package ch.ethz.idsc.owl.data.nd;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** the query {@link NdTreeMap#buildCluster(NdCenterInterface, int)}
 * can be used in parallel. */
public class NdTreeMap<V> implements NdMap<V>, Serializable {
  private final int maxDensity;
  private final int maxDepth;
  private final Tensor global_lBounds;
  private final Tensor global_uBounds;
  // ---
  // reused during adding as well as searching:
  private int size;
  private Node root; // non final because of clear()

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
    VectorQ.require(lbounds);
    VectorQ.require(ubounds);
    if (lbounds.length() != ubounds.length())
      throw TensorRuntimeException.of(lbounds, ubounds);
    if (!IntStream.range(0, lbounds.length()).allMatch(index -> Scalars.lessEquals(lbounds.Get(index), ubounds.Get(index))))
      throw TensorRuntimeException.of(lbounds, ubounds);
    global_lBounds = lbounds.unmodifiable();
    global_uBounds = ubounds.unmodifiable();
    this.maxDensity = maxDensity;
    this.maxDepth = maxDepth;
    clear();
  }

  /** @param location vector with same length as lbounds and ubounds
   * @param value */
  @Override // from NdMap
  public void add(Tensor location, V value) {
    VectorQ.requireLength(location, global_lBounds.length());
    add(new NdPair<>(location, value));
  }

  private synchronized void add(NdPair<V> ndPair) {
    root.add(ndPair, new NdBounds(global_lBounds, global_uBounds));
    ++size;
  }

  @Override // from NdMap
  public int size() {
    return size;
  }

  @Override // from NdMap
  public NdCluster<V> buildCluster(NdCenterInterface ndCenter, int limit) {
    NdCluster<V> cluster = new NdCluster<>(ndCenter, limit);
    root.addToCluster(cluster, new NdBounds(global_lBounds, global_uBounds));
    return cluster;
  }

  @Override // from NdMap
  public synchronized void clear() {
    root = null;
    size = 0;
    root = new Node(maxDepth);
  }

  @Override // from NdMap
  public boolean isEmpty() {
    return size() == 0;
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

  private class Node implements Serializable {
    private final int depth;
    private Node lChild;
    private Node rChild;
    /** queue is set to null when node transform from leaf node to interior node */
    private Queue<NdPair<V>> queue = new ArrayDeque<>();

    private Node(int depth) {
      this.depth = depth;
    }

    private boolean isInternal() {
      return Objects.isNull(queue);
    }

    private int dimension() {
      return depth % global_lBounds.length();
    }

    private void add(final NdPair<V> ndPair, NdBounds ndBounds) {
      if (isInternal()) {
        Tensor location = ndPair.location;
        int dimension = dimension();
        Scalar median = ndBounds.median(dimension);
        if (Scalars.lessThan(location.Get(dimension), median)) {
          ndBounds.uBounds.set(median, dimension);
          if (Objects.isNull(lChild))
            lChild = new Node(depth - 1);
          lChild.add(ndPair, ndBounds);
          return;
        }
        ndBounds.lBounds.set(median, dimension);
        if (Objects.isNull(rChild))
          rChild = new Node(depth - 1);
        rChild.add(ndPair, ndBounds);
      } else //
      if (queue.size() < maxDensity)
        queue.add(ndPair);
      else //
      if (depth == 1)
        queue.add(ndPair);
      // the original code removed a node from the queue: return queue.poll();
      // in our opinion this behavior is undesired.
      // at the lowest depth we grow the queue indefinitely, instead.
      else {
        int dimension = dimension();
        Scalar median = ndBounds.median(dimension);
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
        add(ndPair, ndBounds);
      }
    }

    private void addToCluster(NdCluster<V> cluster, NdBounds ndBounds) {
      if (isInternal()) {
        final int dimension = dimension();
        Scalar median = ndBounds.median(dimension);
        boolean lFirst = Scalars.lessThan(cluster.center.Get(dimension), median);
        addChildToCluster(cluster, ndBounds, median, lFirst);
        addChildToCluster(cluster, ndBounds, median, !lFirst);
      } else
        queue.forEach(cluster::consider);
    }

    private void addChildToCluster(NdCluster<V> cluster, NdBounds ndBounds, Scalar median, boolean left) {
      final int dimension = dimension();
      if (left) {
        if (Objects.isNull(lChild))
          return;
        // ---
        Scalar copy = ndBounds.uBounds.Get(dimension);
        ndBounds.uBounds.set(median, dimension);
        if (cluster.isViable(ndBounds))
          lChild.addToCluster(cluster, ndBounds);
        ndBounds.uBounds.set(copy, dimension);
      } else {
        if (Objects.isNull(rChild))
          return;
        // ---
        Scalar copy = ndBounds.lBounds.Get(dimension);
        ndBounds.lBounds.set(median, dimension);
        if (cluster.isViable(ndBounds))
          rChild.addToCluster(cluster, ndBounds);
        ndBounds.lBounds.set(copy, dimension);
      }
    }
  }

  /***************************************************/
  // functions for testing
  /* package */ void print() {
    print(root);
  }

  private void print(Node node) {
    String v = spaces(root.depth - node.depth);
    if (Objects.isNull(node.queue)) {
      System.out.println(v + "<empty>");
      if (Objects.nonNull(node.lChild))
        print(node.lChild);
      if (Objects.nonNull(node.rChild))
        print(node.rChild);
    } else {
      GlobalAssert.that(Objects.isNull(node.lChild));
      GlobalAssert.that(Objects.isNull(node.rChild));
      System.out.println(v + Integer.toString(node.queue.size()));
      for (NdPair<V> entry : node.queue)
        System.out.println(v + entry.location.toString() + " " + entry.value());
    }
  }

  // helper function
  private static String spaces(int level) {
    return Stream.generate(() -> " ").limit(level).collect(Collectors.joining());
  }
}
