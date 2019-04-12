// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.demo.order.TensorProductOrder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** Creates minTracker for a lexicographic semiorder.
 * The minimal elements for a lexicographic semiorder is the iteratively constructed set
 * where all elements are discarded which are not minimal w.r.t the first semiorder. Then from this remaining
 * set all elements are discarded which are not minimal with respect to the second semiorder and so on. */
public class LexicographicSemiorderMinTracker implements MinTracker<Tensor> {
  public static LexicographicSemiorderMinTracker withList(Tensor slackVector) {
    return new LexicographicSemiorderMinTracker(slackVector, new LinkedList<>());
  }

  public static LexicographicSemiorderMinTracker withSet(Tensor slackVector) {
    return new LexicographicSemiorderMinTracker(slackVector, new HashSet<>());
  }

  private final Collection<Tensor> candidateSet;
  private final Tensor slackVector;
  private final int dim;

  private LexicographicSemiorderMinTracker(Tensor slackVector, Collection<Tensor> candidateSet) {
    this.candidateSet = candidateSet;
    this.slackVector = slackVector;
    this.dim = slackVector.length();
  }

  /** Filters all elements which are within the slack of the "absolute" minimum.
   * 
   * @param x_i: Coordinate of element x
   * @param threshold = u_min + slack
   * @return true or false */
  public static boolean filterCriterion(Scalar x_i, Scalar threshold) {
    return Scalars.lessEquals(x_i, threshold);
  }

  /** Updates the set of potential future candidates for the minimal set.
   * 
   * An element x is not a candidate if there is an index where one of the current candidates
   * strictly precedes x and in all indices before are the current one has smaller values.
   * 
   * @param x */
  private void updateCandidateSet(Tensor x) {
    Iterator<Tensor> iterator = candidateSet.iterator();
    while (iterator.hasNext()) {
      Tensor current = iterator.next();
      for (int index = 0; index < dim; ++index) {
        OrderComparator<Scalar> semiorderComparator = Semiorder.comparator(slackVector.Get(index));
        OrderComparison semiorder = semiorderComparator.compare(x.Get(index), current.Get(index));
        TensorProductOrder tensorProductOrder = TensorProductOrder.createTensorProductOrder(index + 1);
        OrderComparison productOrder = tensorProductOrder.compare(x.extract(0, index + 1), current.extract(0, index + 1));
        // if x strictly precedes the current object and it is strictly preceding in every coordinate until now, then the current object will be discarded
        if (semiorder.equals(OrderComparison.STRICTLY_PRECEDES) && productOrder.equals(OrderComparison.STRICTLY_PRECEDES)) {
          iterator.remove();
          break;
        }
        // if x strictly succeeding the current object and it is strictly succeeding in every coordinate until now, then x will be discarded
        else if (semiorder.equals(OrderComparison.STRICTLY_SUCCEEDS) && productOrder.equals(OrderComparison.STRICTLY_SUCCEEDS)) {
          return;
        }
      }
    }
    candidateSet.add(x);
  }

  public Collection<Tensor> getCandidateSet() {
    return candidateSet;
  }

  @Override
  public void digest(Tensor x) {
    if (x.length() != dim)
      throw new RuntimeException("Tensor x has wrong dimension");
    if (candidateSet.isEmpty()) {
      candidateSet.add(x);
      return;
    }
    updateCandidateSet(x);
  }

  @Override
  public Collection<Tensor> getMinElements() {
    Collection<Tensor> minElements = candidateSet;
    for (int i = 0; i < dim; ++i) {
      if (minElements.size() == 1)
        return minElements;
      int index = i;
      Scalar u_min = minElements.stream().map(x -> x.Get(index)).min(Scalars::compare).get();
      Scalar slack = slackVector.Get(index);
      minElements = minElements.stream() //
          .filter(x -> filterCriterion(x.Get(index), u_min.add(slack))) //
          .collect(Collectors.toList());
    }
    return minElements;
  }
}
