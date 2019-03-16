// code by astoll, jph
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
/** Tracks minimal elements of an ordered set.
 *
 * @param <T>
 */
public interface MinTracker<T> {
  /** Compares an element x of a set to the current list of minimal elements
   * and modifies the set of minimal elements accordingly.
   * @param x */
  void digest(T x);

  /** Retrieves the current set of minimal elements.
   * @return Collection<T> of minimal elements */
  Collection<T> getMinElements();
}
