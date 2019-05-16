// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.Serializable;
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
import ch.ethz.idsc.tensor.alg.VectorQ;

/* package */ class Pair<K> implements Serializable {
  private final K key;
  private final Tensor value;

  public Pair(K key, Tensor value) {
    this.key = key;
    this.value = value;
  }

  public K key() {
    return key;
  }

  public Tensor value() {
    return value;
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
public class LexicographicSemiorderMinTracker<K> implements Serializable {
  public static <K> LexicographicSemiorderMinTracker<K> withList(Tensor slackVector) {
    return new LexicographicSemiorderMinTracker<>(slackVector, new LinkedList<>(), false);
  }

  public static <K> LexicographicSemiorderMinTracker<K> withSet(Tensor slackVector) {
    return new LexicographicSemiorderMinTracker<>(slackVector, new HashSet<>(), false);
  }

  // ---
  private final Collection<Pair<K>> candidateSet;
  private final Tensor slackVector;
  private final int dim;
  private final List<OrderComparator<Scalar>> semiorderComparators = new ArrayList<>();
  private final List<ProductOrderComparator> productOrderComparators = new ArrayList<>();
  private final boolean onlyBeneficialElements;

  protected LexicographicSemiorderMinTracker(Tensor slackVector, Collection<Pair<K>> candidateSet, boolean onlyBeneficialElements) {
    this.candidateSet = candidateSet;
    this.slackVector = VectorQ.require(slackVector);
    this.dim = slackVector.length();
    this.onlyBeneficialElements = onlyBeneficialElements;
    for (int index = 0; index < dim; ++index) {
      semiorderComparators.add(new ScalarSlackSemiorder(slackVector.Get(index)));
      productOrderComparators.add(TensorProductOrder.comparator(index + 1));
    }
  }

  /** Updates the set of potential future candidates for the minimal set.
   * 
   * An element x is not a candidate if there is an index where one of the current candidates
   * strictly precedes x and in all indices before are the current one has smaller values.
   * 
   * @param key
   * @param x value, e.g. scores of key
   * @return Collection of discarded elements upon digestion */
  public Collection<K> digest(K key, Tensor x) {
    Pair<K> applicantPair = new Pair<>(key, VectorQ.requireLength(x, dim));
    Iterator<Pair<K>> iterator = candidateSet.iterator();
    Collection<K> discardedKeys = new ArrayList<>();
    while (iterator.hasNext()) {
      Pair<K> currentPair = iterator.next();
      // if we are only interested in beneficial elements
      if (onlyBeneficialElements) {
        OrderComparison productOrder = productOrderComparators.get(dim - 1).compare(applicantPair.value(), currentPair.value());
        // if applicantPair is better in all dimensions remove currentPair
        if (productOrder.equals(OrderComparison.STRICTLY_PRECEDES)) {
          discardedKeys.add(currentPair.key());
          iterator.remove();
          continue;
          // if applicantPair is indifferent or worse in all dimension discard applicantPair
        } else if (!productOrder.equals(OrderComparison.INCOMPARABLE)) {
          discardedKeys.add(applicantPair.key());
          return discardedKeys;
        }
      }
      for (int index = 0; index < dim; ++index) {
        OrderComparison semiorder = semiorderComparators.get(index).compare(applicantPair.value().Get(index), currentPair.value().Get(index));
        OrderComparison productOrder = productOrderComparators.get(index).compare(applicantPair.value().extract(0, index + 1),
            currentPair.value().extract(0, index + 1));
        // if x strictly precedes the current object and it is strictly preceding in every coordinate until now, then the current object will be discarded
        if (semiorder.equals(OrderComparison.STRICTLY_PRECEDES) && //
            productOrder.equals(OrderComparison.STRICTLY_PRECEDES)) {
          discardedKeys.add(currentPair.key());
          iterator.remove();
          break;
        }
        // if x strictly succeeding the current object and it is strictly succeeding in every coordinate until now, then x will be discarded
        else //
        if (semiorder.equals(OrderComparison.STRICTLY_SUCCEEDS) && //
            productOrder.equals(OrderComparison.STRICTLY_SUCCEEDS)) {
          discardedKeys.add(applicantPair.key());
          return discardedKeys;
        }
      }
    }
    candidateSet.add(applicantPair);
    return discardedKeys;
  }

  /** Filters all elements which are within the slack of the "absolute" minimum.
   * 
   * @param x_i: Coordinate of element x
   * @param threshold = u_min + slack
   * @return true or false */
  private static boolean filterCriterion(Scalar x_i, Scalar threshold) {
    return Scalars.lessEquals(x_i, threshold);
  }

  /** Hint: only for testing
   * 
   * @return current candidateSet */
  /* package */ Collection<Pair<K>> getCandidateSet() {
    return candidateSet;
  }

  /** @return keys of current candidateSet */
  public Collection<K> getCandidateKeys() {
    return candidateSet.stream().map(Pair::key).collect(Collectors.toList());
  }

  /** @return values of current candidateSet */
  public Collection<Tensor> getCandidateValues() {
    return candidateSet.stream().map(Pair::value).collect(Collectors.toList());
  }

  /** Hint: only for testing
   * 
   * @return pairs of current minimal elements */
  /* package */ Collection<Pair<K>> getMinElements() {
    if (candidateSet.isEmpty())
      return Collections.emptyList();
    Collection<Pair<K>> minElements = candidateSet;
    for (int index = 0; index < dim; ++index) {
      int fi = index;
      Scalar u_min = minElements.stream() //
          .map(pair -> pair.value().Get(fi)) //
          .min(Scalars::compare).get();
      Scalar slack = slackVector.Get(fi);
      minElements = minElements.stream() //
          .filter(pair -> filterCriterion(pair.value().Get(fi), u_min.add(slack))) //
          .collect(Collectors.toList());
    }
    return minElements;
  }

  /** @return current keys of minimal elements */
  public Collection<K> getMinKeys() {
    return getMinElements().stream().map(Pair::key).collect(Collectors.toList());
  }

  /** @return current values of minimal elements */
  public Collection<Tensor> getMinValues() {
    return getMinElements().stream().map(Pair::value).collect(Collectors.toList());
  }

  private final Random random = new Random();

  /** When the current minimal set is non-empty and its cardinality larger than one,
   * we will use the usual lexicographic ordering (without slack) to determine the minimum value.
   * 
   * If there are still two pairs with the same minimum score we will choose randomly.
   * 
   * @return current absolute best pair, may also be null */
  /* package */ Pair<K> getBest() {
    // TODO ANDRE implement Pair<K> in usual Tracker as well and use here
    // TODO ANDRE implement with optional
    if (candidateSet.isEmpty())
      return null;
    List<Pair<K>> bestElements = new ArrayList<>(getMinElements());
    for (int index = 0; index < dim; ++index) {
      int fi = index;
      Scalar u_min = bestElements.stream() //
          .map(pair -> pair.value().Get(fi)) //
          .min(Scalars::compare).get();
      bestElements = bestElements.stream() //
          .filter(pair -> pair.value().Get(fi).equals(u_min)) //
          .collect(Collectors.toList());
    }
    // if (bestElements.size() != 1)
    // System.out.println("random choice");
    return bestElements.get(random.nextInt(bestElements.size()));
  }

  /** Gives the key of the absolute best element and deletes the best element from
   * the candidate set
   * 
   * @return key of absolute best pair
   * @throws Exception if min set is empty */
  public K pollBestKey() {
    Pair<K> pair = getBest();
    boolean removed = candidateSet.remove(pair);
    if (!removed)
      System.err.println("could not remove pair=" + pair);
    return pair.key();
  }

  /** @return key of the current absolute best pair */
  public K peekBestKey() {
    Pair<K> best = getBest();
    return Objects.isNull(best) //
        ? null
        : best.key();
  }

  /** @return value of the current absolute best pair */
  public Tensor peekBestValue() {
    Pair<K> best = getBest();
    return Objects.isNull(best) //
        ? null
        : best.value();
  }
}
