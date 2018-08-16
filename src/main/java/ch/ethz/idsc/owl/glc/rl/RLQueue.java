// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public class RLQueue implements Iterable<GlcNode> {
  private final Set<GlcNode> set = new HashSet<>();
  private final Tensor slack;
  protected final int vectorSize;

  public RLQueue(Tensor slack) {
    this.slack = slack;
    this.vectorSize = slack.length();
  }

  public final boolean add(GlcNode glcNode) {
    return set.add(glcNode);
  }

  public final GlcNode poll() {
    GlcNode best = getFromBest();
    remove(best);
    return best;
  }

  public final GlcNode peek() {
    return getFromBest();
  }

  public final boolean removeAll(Collection<GlcNode> collection) {
    return set.removeAll(collection);
  }

  public final boolean isEmpty() {
    return set.isEmpty();
  }

  public Stream<GlcNode> stream() {
    return set.stream();
  }

  public Collection<GlcNode> collection() {
    return Collections.unmodifiableCollection(set);
  }

  @Override // from Iterable
  public final Iterator<GlcNode> iterator() {
    return set.iterator();
  }

  // not used outside class
  private boolean remove(GlcNode glcNode) {
    return set.remove(glcNode);
  }

  /** @return first element from best set
   * @throws Exception if queue is empty */
  private GlcNode getFromBest() {
    List<GlcNode> queueCopy = new ArrayList<>(set);
    getBestSet(queueCopy, 0);
    return queueCopy.get(0);
  }

  /** iteratively find best set
   * 
   * @param list is modified by function
   * @param d level
   * @return list with inferior nodes removed
   * @throws Exception if queue is empty */
  private List<GlcNode> getBestSet(List<GlcNode> list, int d) {
    GlcNode minCostNode = Collections.min(list, new Comparator<GlcNode>() {
      @Override
      public int compare(GlcNode first, GlcNode second) {
        return Scalars.compare( //
            ((VectorScalar) first.merit()).vector().Get(d), //
            ((VectorScalar) second.merit()).vector().Get(d));
      }
    });
    Scalar minMerit = ((VectorScalar) minCostNode.merit()).vector().Get(d);
    Scalar threshold = minMerit.add(slack.Get(d));
    list.removeIf(node -> Scalars.lessThan(threshold, ((VectorScalar) node.merit()).vector().Get(d)));
    if (d == vectorSize - 1)
      return list;
    return getBestSet(list, d + 1);
  }
}
