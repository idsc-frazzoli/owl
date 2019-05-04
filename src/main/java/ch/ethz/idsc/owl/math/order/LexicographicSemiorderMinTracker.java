// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.demo.order.TensorProductOrder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

class Pair<K> {
  public K key;
  public Tensor value;

  public Pair(K key, Tensor value) {
    this.key = key;
    this.value = value;
  }
}

/** Creates minTracker for a lexicographic semiorder.
 * The minimal elements for a lexicographic semiorder is the iteratively constructed set
 * where all elements are discarded which are not minimal w.r.t the first semiorder. Then from this remaining
 * set all elements are discarded which are not minimal with respect to the second semiorder and so on. */
public class LexicographicSemiorderMinTracker<K> {
  public static <K> LexicographicSemiorderMinTracker<K> withList(Tensor slackVector) {
    return new LexicographicSemiorderMinTracker<>(slackVector, new LinkedList<>());
  }

  public static <K> LexicographicSemiorderMinTracker<K> withSet(Tensor slackVector) {
    return new LexicographicSemiorderMinTracker<>(slackVector, new HashSet<>());
  }

  // ---
  private final Collection<Pair<K>> candidateSet;
  private final Tensor slackVector;
  private final int dim;
  private final List<OrderComparator<Scalar>> semiorderComparators;
  private final List<ProductOrderComparator> productOrderComparators;

  private LexicographicSemiorderMinTracker(Tensor slackVector, Collection<Pair<K>> candidateSet) {
    this.candidateSet = candidateSet;
    this.slackVector = slackVector;
    this.dim = slackVector.length();
    List<OrderComparator<Scalar>> semiorderComparators = new ArrayList<>();
    List<ProductOrderComparator> productOrderComparators = new ArrayList<>();
    for (int index = 0; index < dim; ++index) {
      semiorderComparators.add(new ScalarSlackSemiorder(slackVector.Get(index)));
      productOrderComparators.add(TensorProductOrder.comparator(index + 1));
    }
    this.semiorderComparators = semiorderComparators;
    this.productOrderComparators = productOrderComparators;
  }

  /** Updates the set of potential future candidates for the minimal set.
   * 
   * An element x is not a candidate if there is an index where one of the current candidates
   * strictly precedes x and in all indices before are the current one has smaller values.
   * 
   * @param x */
  private Collection<K> updateCandidateSet(Pair<K> applicantPair) {
    Iterator<Pair<K>> iterator = candidateSet.iterator();
    Collection<K> discardedKeys = new ArrayList<>();
    while (iterator.hasNext()) {
      Pair<K> currentPair = iterator.next();
      for (int index = 0; index < dim; ++index) {
        OrderComparison semiorder = semiorderComparators.get(index).compare(applicantPair.value.Get(index), currentPair.value.Get(index));
        OrderComparison productOrder = productOrderComparators.get(index).compare(applicantPair.value.extract(0, index + 1),
            currentPair.value.extract(0, index + 1));
        // if x strictly precedes the current object and it is strictly preceding in every coordinate until now, then the current object will be discarded
        if (semiorder.equals(OrderComparison.STRICTLY_PRECEDES) && //
            productOrder.equals(OrderComparison.STRICTLY_PRECEDES)) {
          discardedKeys.add(currentPair.key);
          iterator.remove();
          break;
        }
        // if x strictly succeeding the current object and it is strictly succeeding in every coordinate until now, then x will be discarded
        else //
        if (semiorder.equals(OrderComparison.STRICTLY_SUCCEEDS) && //
            productOrder.equals(OrderComparison.STRICTLY_SUCCEEDS)) {
          discardedKeys.add(applicantPair.key);
          return discardedKeys;
        }
      }
    }
    candidateSet.add(applicantPair);
    return discardedKeys;
  }

  public Collection<Pair<K>> getCandidateSet() {
    return candidateSet;
  }

  public Collection<K> digest(K key, Tensor x) {
    if (x.length() != dim)
      throw new RuntimeException("Tensor x has wrong dimension");
    Pair<K> applicantPair = new Pair<>(key, x);
    if (candidateSet.isEmpty()) {
      candidateSet.add(applicantPair);
      return Collections.emptyList();
    }
    return updateCandidateSet(applicantPair);
  }

  public Collection<Pair<K>> getMinElements() {
    Collection<Pair<K>> minElements = candidateSet;
    for (int index = 0; index < dim; ++index) {
      if (minElements.size() == 1)
        return minElements;
      int fi = index;
      Scalar u_min = minElements.stream().map(x -> x.value.Get(fi)).min(Scalars::compare).get();
      Scalar slack = slackVector.Get(fi);
      minElements = minElements.stream() //
          .filter(x -> filterCriterion(x.value.Get(fi), u_min.add(slack))) //
          .collect(Collectors.toList());
    }
    return minElements;
  }

  /** Filters all elements which are within the slack of the "absolute" minimum.
   * 
   * @param x_i: Coordinate of element x
   * @param threshold = u_min + slack
   * @return true or false */
  private static boolean filterCriterion(Scalar x_i, Scalar threshold) {
    return Scalars.lessEquals(x_i, threshold);
  }
}
