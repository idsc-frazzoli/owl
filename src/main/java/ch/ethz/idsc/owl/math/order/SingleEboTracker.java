// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import ch.ethz.idsc.owl.demo.order.ScalarTotalOrder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Creates EBO (elimination by objective) tracker for a lexicographic semiorder.
 * The EBO procedure chooses a the "best" element from a given set according to the underlying lexicographic semiorder.
 * Only keeps track of necessary elements for a single choice.
 * 
 * <p>For a detailed description of the procedure, see
 * "Multi-Objective Optimization Using Preference Structures", Chapter 6.1 */
public class SingleEboTracker<K> extends AbstractEboTracker<K> {
  /** @param slacks
   * @return
   * @throws Exception if given slacks is null */
  public static <K> EboTracker<K> withList(Tensor slacks) {
    return new SingleEboTracker<>(slacks, new LinkedList<>());
  }

  /** @param slacks
   * @return
   * @throws Exception if given slacks is null */
  public static <K> EboTracker<K> withSet(Tensor slacks) {
    return new SingleEboTracker<>(slacks, new HashSet<>());
  }

  /***************************************************/
  private final ProductOrderComparator productOrderComparator;

  private SingleEboTracker(Tensor slacks, Collection<Pair<K>> candidateSet) {
    super(slacks, candidateSet);
    productOrderComparator = TensorProductOrder.comparator(dim);
  }

  @Override // from AbstractLexSemiMinTracker
  protected void trim(Pair<K> applicantPair, Collection<Pair<K>> candidateSet, Collection<K> discardedKeys) {
    Iterator<Pair<K>> iterator = candidateSet.iterator();
    while (iterator.hasNext()) {
      Pair<K> currentPair = iterator.next();
      { // we are only interested in beneficial elements
        OrderComparison productComparison = productOrderComparator.compare(applicantPair.value(), currentPair.value());
        // if applicantPair is better in all dimensions remove currentPair
        if (productComparison.equals(OrderComparison.STRICTLY_PRECEDES)) {
          discardedKeys.add(currentPair.key());
          iterator.remove();
          continue; // continue with while loop
          // if applicantPair is indifferent or worse in all dimension discard applicantPair
        } else //
        if (!productComparison.equals(OrderComparison.INCOMPARABLE)) {
          discardedKeys.add(applicantPair.key());
          return;
        }
      }
      // the code below is identical to LexicographicSemiorderMinTracker::digest
      ProductOrderTracker<Scalar> productOrderTracker = new ProductOrderTracker<>(ScalarTotalOrder.INSTANCE);
      for (int index = 0; index < dim; ++index) {
        Scalar x = applicantPair.value().Get(index);
        Scalar y = currentPair.value().Get(index);
        OrderComparison semiorder = semiorderComparators.get(index).compare(x, y); // uses ScalarSlackSemiorder
        OrderComparison productorder = productOrderTracker.digest(x, y); // uses ScalarTotalOrder
        // if x strictly precedes the current object and it is strictly preceding
        // in every coordinate until now, then the current object will be discarded
        if (semiorder.equals(OrderComparison.STRICTLY_PRECEDES)) {
          if (productorder.equals(OrderComparison.STRICTLY_PRECEDES)) {
            discardedKeys.add(currentPair.key());
            iterator.remove();
            break; // leave for loop, continue with while loop
          }
        } else //
        // if x strictly succeeding the current object and it is strictly succeeding
        // in every coordinate until now, then x will be discarded
        if (semiorder.equals(OrderComparison.STRICTLY_SUCCEEDS)) {
          if (productorder.equals(OrderComparison.STRICTLY_SUCCEEDS)) {
            discardedKeys.add(applicantPair.key());
            return;
          }
        }
      }
    }
    candidateSet.add(applicantPair);
  }
}
