// code by jph
package ch.ethz.idsc.owl.data.nd;

import ch.ethz.idsc.tensor.Tensor;

/** NdMap contains (coordinate, value)-pairs.
 * multiple values can be associated to the same coordinate.
 * 
 * @param <V> */
public interface NdMap<V> {
  /** @param location
   * @param value */
  void add(Tensor location, V value);

  /** @return number of entries stored in map */
  int size();

  /** the application layer should not make assumptions
   * on the ordering of the points in the cluster
   * 
   * @param ndCenter
   * @param limit strictly positive
   * @return cluster of no more than limit closest points to given ndCenter */
  NdCluster<V> buildCluster(NdCenterInterface ndCenter, int limit);

  /** clears all entries from map */
  void clear();

  /** @return true if size() == 0 */
  default boolean isEmpty() {
    return size() == 0;
  }
}
