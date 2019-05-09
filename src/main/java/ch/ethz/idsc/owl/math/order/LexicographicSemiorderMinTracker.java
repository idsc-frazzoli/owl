// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
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

  @Override
  public String toString() {
    return key + " " + value;
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
   * @param applicantPair new digested element
   * @return Collection of discarded elements upon update step */
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

  /** @param key
   * @param x value, e.g. scores of key
   * @return Collection of discarded elements upon digestion */
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

  private void deleteElement(Pair<K> pair) {
    if (candidateSet.contains(pair))
      candidateSet.remove(pair);
    else
      System.err.println("warning: could not delete pair " + pair);
  }

  /** Filters all elements which are within the slack of the "absolute" minimum.
   * 
   * @param x_i: Coordinate of element x
   * @param threshold = u_min + slack
   * @return true or false */
  private static boolean filterCriterion(Scalar x_i, Scalar threshold) {
    return Scalars.lessEquals(x_i, threshold);
  }

  /** @return current cnadidateSet */
  public Collection<Pair<K>> getCandidateSet() {
    return candidateSet;
  }

  /** @return keys of current candidateSet */
  public Collection<K> getCandidateKeys() {
    return getKeys(candidateSet);
  }

  /** @return values of current candidateSet */
  public Collection<Tensor> getCandidateValues() {
    return getValues(candidateSet);
  }

  /** @return pairs of current minimal elements */
  public Collection<Pair<K>> getMinElements() {
    if (candidateSet.isEmpty())
      return Collections.emptyList();
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

  /** @return current keys of minimal elements */
  public Collection<K> getMinKeys() {
    if (candidateSet.isEmpty())
      return Collections.emptyList();
    return getKeys(getMinElements());
  }

  /** @return current values of minimal elements */
  public Collection<Tensor> getMinValues() {
    if (candidateSet.isEmpty())
      return Collections.emptyList();
    return getValues(getMinElements());
  }

  private final Random random = new Random();

  /** When the current minimal set is non-empty and its cardinality larger than one,
   * we will use the usual lexicographic ordering (without slack) to determine the minimum value.
   * 
   * If there are still two pairs with the same minimum score we will choose randomly.
   * 
   * @return current absolute best pair, may also be null */
  public Pair<K> getBest() {
    // TODO ANDRE implement Pair<K> in usual Tracker as well and use here
    // TODO implement with optional
    if (candidateSet.isEmpty())
      return null;
    List<Pair<K>> bestElements = new ArrayList<>(getMinElements());
    for (int index = 0; index < dim; ++index) {
      if (bestElements.size() == 1)
        // FIXME JAN is this best way to do it?
        return bestElements.get(0);
      int fi = index;
      Scalar u_min = bestElements.stream().map(x -> x.value.Get(fi)).min(Scalars::compare).get();
      bestElements = bestElements.stream() //
          .filter(x -> x.value.Get(fi).equals(u_min)) //
          .collect(Collectors.toList());
    }
    // if (bestElements.size() != 1)
    // System.out.println("random choice");
    return bestElements.get(random.nextInt(bestElements.size()));
  }

  /** Gives the key of the absolute best element and deletes the best element from
   * the candidate set
   * 
   * @return key of absolute best pair */
  public K pollBestKey() {
    Pair<K> p = getBest();
    deleteElement(p);
    return p.key;
  }

  /** @return key of the current absolute best pair */
  public K peekBestKey() {
    Pair<K> best = getBest();
    return Objects.isNull(best) //
        ? null
        : best.key;
  }

  /** @return value of the current absolute best pair */
  public Tensor peekBestValue() {
    Pair<K> best = getBest();
    return Objects.isNull(best) //
        ? null
        : best.value;
  }

  /** @param pairs Collection of pairs
   * @return List of key of given pairs */
  public List<K> getKeys(Collection<Pair<K>> pairs) {
    Iterator<Pair<K>> iterator = pairs.iterator();
    List<K> keyList = new ArrayList<>();
    while (iterator.hasNext())
      keyList.add(iterator.next().key);
    return keyList;
  }

  /** @param pairs Collection of pairs
   * @return List of values of given pairs */
  public List<Tensor> getValues(Collection<Pair<K>> pairs) {
    Iterator<Pair<K>> iterator = pairs.iterator();
    List<Tensor> valueList = new ArrayList<>();
    while (iterator.hasNext())
      valueList.add(iterator.next().value);
    return valueList;
  }
}
