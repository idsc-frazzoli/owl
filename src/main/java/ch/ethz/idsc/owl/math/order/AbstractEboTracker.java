// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.ethz.idsc.subare.util.RandomChoice;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** immutable */
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
    return "Pair[" + key + " -> " + value + "]";
  }
}

/** Creates EBO (elimination by objective) tracker for a lexicographic semiorder.
 * The EBO procedure chooses a the "best" element from a given set according to the underlying lexicographic semiorder.
 * 
 * For a detailed description of the procedure, see
 * "Multi-Objective Optimization Using Preference Structures", Chapter 6.1 */
public abstract class AbstractEboTracker<K> implements EboTracker<K>, Serializable {
  private final Tensor slacks;
  private final Collection<Pair<K>> candidateSet;
  protected final int dim;
  protected final List<OrderComparator<Scalar>> semiorderComparators = new ArrayList<>();

  protected AbstractEboTracker(Tensor slacks, Collection<Pair<K>> candidateSet) {
    this.slacks = VectorQ.require(slacks);
    this.candidateSet = candidateSet;
    this.dim = slacks.length();
    for (int index = 0; index < dim; ++index)
      semiorderComparators.add(new ScalarSlackSemiorder(slacks.Get(index)));
  }

  /** Hint: only for testing
   * 
   * @return current candidateSet */
  /* package */ final Collection<Pair<K>> getCandidateSet() {
    return candidateSet;
  }

  /** @return keys of current candidateSet */
  protected final Collection<K> getCandidateKeys() {
    return candidateSet.stream().map(Pair::key).collect(Collectors.toList());
  }

  /** @return values of current candidateSet */
  protected final Collection<Tensor> getCandidateValues() {
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
          .map(Pair::value) //
          .map(vector -> vector.Get(fi)) //
          .min(Scalars::compare).get();
      Scalar threshold = u_min.add(slacks.Get(fi));
      minElements = minElements.stream() //
          .filter(pair -> Scalars.lessEquals(pair.value().Get(fi), threshold)) //
          .collect(Collectors.toList());
    }
    return minElements;
  }

  /** @return current keys of minimal elements */
  protected final Collection<K> getMinKeys() {
    return getMinElements().stream().map(Pair::key).collect(Collectors.toList());
  }

  /** @return current values of minimal elements */
  protected final Collection<Tensor> getMinValues() {
    return getMinElements().stream().map(Pair::value).collect(Collectors.toList());
  }

  /** When the current minimal set is non-empty and its cardinality larger than one,
   * we will use the usual lexicographic ordering (without slack) to determine the minimum value.
   * 
   * If there are still two pairs with the same minimum score we will choose randomly.
   * 
   * @return current absolute best pair, may also be null */
  /* package */ final Pair<K> getBest() {
    if (candidateSet.isEmpty())
      return null;
    List<Pair<K>> bestElements = new ArrayList<>(getMinElements());
    for (int index = 0; index < dim; ++index) {
      int fi = index;
      Scalar u_min = bestElements.stream() //
          .map(Pair::value) //
          .map(pair -> pair.Get(fi)) //
          .min(Scalars::compare).get();
      bestElements = bestElements.stream() //
          .filter(pair -> pair.value().Get(fi).equals(u_min)) //
          .collect(Collectors.toList());
    }
    return RandomChoice.of(bestElements);
  }

  @Override // from LexSemiMinTracker
  public final Collection<K> digest(K key, Tensor x) {
    Collection<K> discardedKeys = new ArrayList<>();
    trim(new Pair<>(key, VectorQ.requireLength(x, dim)), candidateSet, discardedKeys);
    return discardedKeys;
  }

  /** @param applicantPair
   * @param candidateSet
   * @param discardedKeys */
  protected abstract void trim(Pair<K> applicantPair, Collection<Pair<K>> candidateSet, Collection<K> discardedKeys);

  @Override // from LexSemiMinTracker
  public final K pollBestKey() {
    Pair<K> pair = getBest();
    boolean removed = candidateSet.remove(pair);
    if (!removed)
      throw new RuntimeException("could not remove " + pair);
    return pair.key();
  }

  @Override // from LexSemiMinTracker
  public final K peekBestKey() {
    Pair<K> best = getBest();
    return Objects.isNull(best) //
        ? null
        : best.key();
  }
}
