// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** Adapted version of the LexicographicSemiorderMinTracker which only tracks elements which
 * have advantageous or beneficial scores, i.e. any element with scores that are worse in all objectives
 * compared to any existing element in the tracker will be discarded regardless whether or not they
 * are within the threshold.
 * 
 * @param <K> key type */
public class DMLexSemiMinTracker<K> extends AbstractLexSemiMinTracker<K> {
  /** @param slacks
   * @return
   * @throws Exception if given slacks is null */
  public static <K> LexSemiMinTracker<K> withList(Tensor slacks) {
    return new DMLexSemiMinTracker<>(slacks, new LinkedList<>());
  }

  /** @param slacks
   * @return
   * @throws Exception if given slacks is null */
  public static <K> LexSemiMinTracker<K> withSet(Tensor slacks) {
    return new DMLexSemiMinTracker<>(slacks, new HashSet<>());
  }

  // ---
  private DMLexSemiMinTracker(Tensor slackVector, Collection<Pair<K>> candidateSet) {
    super(slackVector, candidateSet);
  }

  @Override // from AbstractLexSemiMinTracker
  public Collection<K> digest(K key, Tensor x) {
    Pair<K> applicantPair = new Pair<>(key, VectorQ.requireLength(x, dim));
    Iterator<Pair<K>> iterator = candidateSet.iterator();
    Collection<K> discardedKeys = new ArrayList<>();
    while (iterator.hasNext()) {
      Pair<K> currentPair = iterator.next();
      { // we are only interested in beneficial elements
        OrderComparison productOrder = productOrderComparators.get(dim - 1).compare(applicantPair.value(), currentPair.value());
        // if applicantPair is better in all dimensions remove currentPair
        if (productOrder.equals(OrderComparison.STRICTLY_PRECEDES)) {
          discardedKeys.add(currentPair.key());
          iterator.remove();
          continue;
          // if applicantPair is indifferent or worse in all dimension discard applicantPair
        } else //
        if (!productOrder.equals(OrderComparison.INCOMPARABLE)) {
          discardedKeys.add(applicantPair.key());
          return discardedKeys;
        }
      }
      // the code below is identical to LexicographicSemiorderMinTracker::digest
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
}
