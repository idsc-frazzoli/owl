// code by gjoel
package ch.ethz.idsc.owl.math.planar;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public class QuadTree {
  private final static int DEFAULT_LEVELS = 10;
  // ---
  private final QuadNode top;

  /** @param clipX
   * @param clipY
   * @param capacity of single cell */
  public QuadTree(Clip clipX, Clip clipY, int capacity) {
    this(clipX, clipY, capacity, DEFAULT_LEVELS);
  }

  /** @param clipX
   * @param clipY
   * @param capacity of single cell
   * @param levels max depth */
  public QuadTree(Clip clipX, Clip clipY, int capacity, int levels) {
    Tensor center = Tensors.of( //
        clipX.min().add(clipX.width().multiply(RationalScalar.HALF)), //
        clipY.min().add(clipY.width().multiply(RationalScalar.HALF)));
    top = new QuadNode(center, Tensors.of(clipX.width(), clipY.width()), 0, levels, capacity);
  }

  /** @param vector
   * @return whether is in tree area */
  public boolean contains(Tensor vector) {
    return top.contains(VectorQ.require(vector));
  }

  public void insert(Tensor vector) {
    if (contains(vector))
      top.insert(vector);
    else
      throw TensorRuntimeException.of(vector);
  }

  public void insert(Tensor... vectors) {
    Stream.of(vectors).forEach(this::insert);
  }

  /** @param vector reference
   * @return closest point to reference point */
  public Optional<Tensor> closest(Tensor vector) {
    return closest(vector, true);
  }

  /** @param vector reference
   * @param exact wheter to use exact point or cell center
   * @return closest point to reference point */
  public Optional<Tensor> closest(Tensor vector, boolean exact) {
    if (contains(vector))
      return Optional.of(top.closest(vector, exact));
    System.err.println(vector + " is not inside " + this.toString());
    return Optional.empty();
  }
}

/* package */ class QuadNode {
  private static final Tensor LOCATIONS = Tensors.fromString("{{1/2, 1/2}, {1/2, -1/2},{-1/2, 1/2},{-1/2, -1/2}}");
  // ---
  private final Tensor center;
  private final Clip clipX;
  private final Clip clipY;
  private final int level;
  private final int maxLevel;
  private final int capacity;
  private final QuadNode[] children = new QuadNode[4];
  private final Set<Tensor> points = new HashSet<>();
  private boolean empty = true;

  /** @param center of cell
   * @param dimensions of cell
   * @param level current depth
   * @param maxLevel maximum depth
   * @param capacity of cell */
  /* package */ QuadNode(Tensor center, Tensor dimensions, int level, int maxLevel, int capacity) {
    this.center = center;
    this.level = level;
    this.maxLevel = maxLevel;
    this.capacity = capacity;
    Scalar halfX = dimensions.Get(0).multiply(RationalScalar.HALF);
    Scalar halfY = dimensions.Get(1).multiply(RationalScalar.HALF);
    clipX = Clips.interval(center.Get(0).subtract(halfX), center.Get(0).add(halfX));
    clipY = Clips.interval(center.Get(1).subtract(halfY), center.Get(1).add(halfY));
  }

  public boolean isEmpty() {
    return empty;
  }

  public boolean contains(Tensor vector) {
    return clipX.isInside(vector.Get(0)) //
        && clipY.isInside(vector.Get(1));
  }

  public void insert(Tensor vector) {
    if (contains(vector)) {
      empty = false;
      if (Objects.nonNull(children[0])) {
        for (QuadNode child : children)
          child.insert(vector);
      } else {
        points.add(vector);
        if (capacity < points.size() && level < maxLevel)
          split();
      }
    }
  }

  private void split() {
    Tensor size = Tensors.of( //
        center.Get(0).subtract(clipX.min()), //
        center.Get(1).subtract(clipY.min()));
    AtomicInteger count = new AtomicInteger();
    LOCATIONS.stream().map(size::pmul).map(center::add).forEach(center -> //
    children[count.getAndIncrement()] = new QuadNode(center, size, level + 1, maxLevel, capacity));
    // ---
    for (QuadNode child : children)
      points.forEach(child::insert);
    points.clear();
  }

  private Set<Tensor> points() {
    if (Objects.nonNull(children[0])) {
      Set<Tensor> points = new HashSet<>();
      for (QuadNode child : children)
        points.addAll(child.points());
      return points;
    }
    return Collections.unmodifiableSet(points);
  }

  /** @param vector reference
   * @param exact wheter to use exact point or cell center
   * @return closest point to reference point */
  /* package */ Tensor closest(Tensor vector, boolean exact) {
    Tensor neighbours = Tensors.empty();
    if (points.isEmpty())
      neighbours = Tensor.of(Arrays.stream(children).filter(node -> node.contains(vector) && !node.isEmpty()).map(node -> node.closest(vector, exact)));
    if (Tensors.isEmpty(neighbours))
      neighbours = Tensor.of(points().stream());
    if (exact) {
      int idx = ArgMin.of(Tensor.of(neighbours.stream().map(vector::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector)));
      return neighbours.get(idx);
    }
    return center;
  }
}
