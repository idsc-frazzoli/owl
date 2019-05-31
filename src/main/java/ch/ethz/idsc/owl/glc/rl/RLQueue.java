// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.sophus.VectorScalars;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class RLQueue implements Iterable<GlcNode> {
  private final Set<GlcNode> set = new HashSet<>();
  private final Tensor slack;

  public RLQueue(Tensor slack) {
    this.slack = slack;
  }

  public final boolean add(GlcNode glcNode) {
    return set.add(glcNode);
  }

  public final GlcNode poll() {
    GlcNode best = getFromBest();
    set.remove(best);
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

  public final Stream<GlcNode> stream() {
    return set.stream();
  }

  public final Collection<GlcNode> collection() {
    return Collections.unmodifiableCollection(set);
  }

  public final int size() {
    return set.size();
  }

  @Override // from Iterable
  public final Iterator<GlcNode> iterator() {
    return set.iterator();
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
    GlcNode minCostNode = StaticHelper.getMin(list, d);
    Scalar minMerit = VectorScalars.at(minCostNode.merit(), d);
    Scalar threshold = minMerit.add(slack.Get(d));
    list.removeIf(node -> Scalars.lessThan(threshold, VectorScalars.at(node.merit(), d)));
    return d < slack.length() - 1 //
        ? getBestSet(list, d + 1)
        : list;
  }
}
