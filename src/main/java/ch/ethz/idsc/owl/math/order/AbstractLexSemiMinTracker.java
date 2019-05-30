// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
public abstract class AbstractLexSemiMinTracker<K> implements Serializable {
  private static final Random RANDOM = new Random();
  protected final Collection<Pair<K>> candidateSet;
  private final Tensor slackVector;
  protected final int dim;
  protected final List<OrderComparator<Scalar>> semiorderComparators = new ArrayList<>();
  protected final List<ProductOrderComparator> productOrderComparators = new ArrayList<>();

  protected AbstractLexSemiMinTracker(Tensor slackVector, Collection<Pair<K>> candidateSet) {
    this.candidateSet = candidateSet;
    this.slackVector = VectorQ.require(slackVector);
    this.dim = slackVector.length();
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
   * @return collection of discarded elements upon digestion */
  public abstract Collection<K> digest(K key, Tensor x);

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
  /* package */ final Collection<Pair<K>> getCandidateSet() {
    return candidateSet;
  }

  /** @return keys of current candidateSet */
  public final Collection<K> getCandidateKeys() {
    return candidateSet.stream().map(Pair::key).collect(Collectors.toList());
  }

  /** @return values of current candidateSet */
  public final Collection<Tensor> getCandidateValues() {
    return candidateSet.stream().map(Pair::value).collect(Collectors.toList());
  }

  /** Hint: only for testing
   * 
   * @return pairs of current minimal elements */
  /* package */ final Collection<Pair<K>> getMinElements() {
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
  public final Collection<K> getMinKeys() {
    return getMinElements().stream().map(Pair::key).collect(Collectors.toList());
  }

  /** @return current values of minimal elements */
  public final Collection<Tensor> getMinValues() {
    return getMinElements().stream().map(Pair::value).collect(Collectors.toList());
  }

  /** When the current minimal set is non-empty and its cardinality larger than one,
   * we will use the usual lexicographic ordering (without slack) to determine the minimum value.
   * 
   * If there are still two pairs with the same minimum score we will choose randomly.
   * 
   * @return current absolute best pair, may also be null */
  /* package */ final Pair<K> getBest() {
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
    return bestElements.get(RANDOM.nextInt(bestElements.size()));
  }

  /** Gives the key of the absolute best element and deletes the best element from
   * the candidate set
   * 
   * @return key of absolute best pair
   * @throws Exception if min set is empty */
  public final K pollBestKey() {
    Pair<K> pair = getBest();
    boolean removed = candidateSet.remove(pair);
    if (!removed)
      System.err.println("could not remove pair=" + pair);
    return pair.key();
  }

  /** @return key of the current absolute best pair */
  public final K peekBestKey() {
    Pair<K> best = getBest();
    return Objects.isNull(best) //
        ? null
        : best.key();
  }

  /** @return value of the current absolute best pair */
  public final Tensor peekBestValue() {
    Pair<K> best = getBest();
    return Objects.isNull(best) //
        ? null
        : best.value();
  }
}
