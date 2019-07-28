// code by astoll, jph
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;

import ch.ethz.idsc.tensor.Tensor;

public interface LexSemiMinTracker<K> {
  /** Updates the set of potential future candidates for the minimal set.
   * 
   * An element x is not a candidate if there is an index where one of the current candidates
   * strictly precedes x and in all indices before are the current one has smaller values.
   * 
   * @param key
   * @param x value, e.g. scores of key
   * @return collection of discarded elements upon digestion */
  Collection<K> digest(K key, Tensor x);

  /** Gives the key of the absolute best element and deletes the best element from
   * the candidate set
   * 
   * @return key of absolute best pair
   * @throws Exception if min set is empty */
  K pollBestKey();

  /** @return key of the current absolute best pair */
  K peekBestKey();
}
