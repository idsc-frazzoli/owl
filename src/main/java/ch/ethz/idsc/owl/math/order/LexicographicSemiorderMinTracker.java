// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** Creates minTracker for a lexicographic semiorder.
 * The minimal elements for a lexicographic semiorder is the iteratively constructed set
 * where all elements are discarded which are not minimal w.r.t the first semiorder. Then from this remaining
 * set all elements are discarded which are not minimal with respect to the second semiorder and so on. */
public class LexicographicSemiorderMinTracker<K> extends AbstractLexSemiMinTracker<K> {
  public static <K> LexSemiMinTracker<K> withList(Tensor slackVector) {
    return new LexicographicSemiorderMinTracker<>(slackVector, new LinkedList<>());
  }

  public static <K> LexSemiMinTracker<K> withSet(Tensor slackVector) {
    return new LexicographicSemiorderMinTracker<>(slackVector, new HashSet<>());
  }

  // ---
  protected LexicographicSemiorderMinTracker(Tensor slackVector, Collection<Pair<K>> candidateSet) {
    super(slackVector, candidateSet);
  }

  @Override // from AbstractLexSemiMinTracker
  public Collection<K> digest(K key, Tensor x) {
    Pair<K> applicantPair = new Pair<>(key, VectorQ.requireLength(x, dim));
    Iterator<Pair<K>> iterator = candidateSet.iterator();
    Collection<K> discardedKeys = new ArrayList<>();
    while (iterator.hasNext()) {
      Pair<K> currentPair = iterator.next();
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
