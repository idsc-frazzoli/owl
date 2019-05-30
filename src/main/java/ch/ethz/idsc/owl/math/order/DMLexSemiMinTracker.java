// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import ch.ethz.idsc.tensor.Tensor;

/** Adapted version of the LexicographicSemiorderMinTracker which only tracks elements which
 * have advantageous or beneficial scores, i.e. any element with scores that are worse in all objectives
 * compared to any existing element in the tracker will be discarded regardless whether or not they
 * are within the threshold.
 * 
 * @param <K> key type */
public class DMLexSemiMinTracker<K> extends LexicographicSemiorderMinTracker<K> {
  public static <K> DMLexSemiMinTracker<K> withList(Tensor slackVector) {
    return new DMLexSemiMinTracker<>(slackVector, new LinkedList<>());
  }

  public static <K> DMLexSemiMinTracker<K> withSet(Tensor slackVector) {
    return new DMLexSemiMinTracker<>(slackVector, new HashSet<>());
  }

  // ---
  private DMLexSemiMinTracker(Tensor slackVector, Collection<Pair<K>> candidateSet) {
    super(slackVector, candidateSet, true);
  }
}
