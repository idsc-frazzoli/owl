// code by jph
package ch.ethz.idsc.owl.math.region;

/** determines membership for elements of type T */
public interface Region<T> {
  /** @param element
   * @return membership status of given element */
  boolean isMember(T element);
}
