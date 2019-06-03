// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import ch.ethz.idsc.tensor.Tensor;

/** Creates minTracker for a lexicographic semiorder.
 * The minimal elements for a lexicographic semiorder is the iteratively constructed set
 * where all elements are discarded which are not minimal w.r.t the first semiorder. Then from this remaining
 * set all elements are discarded which are not minimal with respect to the second semiorder and so on. */
public class LexicographicSemiorderMinTracker<K> extends AbstractLexSemiMinTracker<K> {
  /** @param slacks
   * @return */
  public static <K> LexSemiMinTracker<K> withList(Tensor slacks) {
    return new LexicographicSemiorderMinTracker<>(slacks, new LinkedList<>());
  }

  /** @param slacks
   * @return */
  public static <K> LexSemiMinTracker<K> withSet(Tensor slacks) {
    return new LexicographicSemiorderMinTracker<>(slacks, new HashSet<>());
  }

  // ---
  private LexicographicSemiorderMinTracker(Tensor slacks, Collection<Pair<K>> candidateSet) {
    super(slacks, candidateSet);
  }

  @Override // from AbstractLexSemiMinTracker
  protected void trim(Pair<K> applicantPair, Collection<Pair<K>> candidateSet, Collection<K> discardedKeys) {
    Iterator<Pair<K>> iterator = candidateSet.iterator();
    while (iterator.hasNext()) {
      Pair<K> currentPair = iterator.next();
      for (int index = 0; index < dim; ++index) {
        OrderComparison semiorder = semiorderComparators.get(index).compare(applicantPair.value().Get(index), currentPair.value().Get(index));
        // if x strictly precedes the current object and it is strictly preceding
        // in every coordinate until now, then the current object will be discarded
        if (semiorder.equals(OrderComparison.STRICTLY_PRECEDES)) { //
          if (productComparison(applicantPair, currentPair, index).equals(OrderComparison.STRICTLY_PRECEDES)) {
            discardedKeys.add(currentPair.key());
            iterator.remove();
            break; // leave for loop, continue with while loop
          }
        } else //
        // if x strictly succeeding the current object and it is strictly succeeding
        // in every coordinate until now, then x will be discarded
        if (semiorder.equals(OrderComparison.STRICTLY_SUCCEEDS)) { //
          if (productComparison(applicantPair, currentPair, index).equals(OrderComparison.STRICTLY_SUCCEEDS)) {
            discardedKeys.add(applicantPair.key());
            return;
          }
        }
      }
    }
    candidateSet.add(applicantPair);
  }
}
